package com.haru.SwipeStyle.Services;

import com.haru.SwipeStyle.Components.ScraperCountdown;
import com.haru.SwipeStyle.DTOs.ClothingDTO;
import com.haru.SwipeStyle.Entities.Clothing;
import com.haru.SwipeStyle.Entities.JobEntity;
import com.haru.SwipeStyle.Mapper.ClothingMapper;
import com.haru.SwipeStyle.Repository.JobRepo;
import com.haru.SwipeStyle.Repository.SwipeStyleRepo;
import com.haru.SwipeStyle.Services.impl.HMScrapingService;
import com.haru.SwipeStyle.Services.impl.ZaraScrapingService;
import jakarta.annotation.PostConstruct;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
public class UrlProvider {
    @Autowired
    JobScheduler jobScheduler;

    @Autowired
    private ClothingService clothingService;

    @Autowired
    SwipeStyleRepo swipeStyleRepo;

    @Autowired
    private ZaraScrapingService zaraScrapingService;

    @Autowired
    private JobRepo jobRepository;

    @Autowired
    private ScraperCountdown scraperCountdown;

    @Autowired
    private HMScrapingService hmScrapingService;

    @Value("#{'${app.service.urls}'.split(',')}")
    private List<String> urls;

    @Value("${app.scraper.zara.maxScrolls:5}")
    private int zaraMaxScrolls;

    @Value("${app.scraper.hm.maxPages:3}")
    private int hmMaxPages;

    @Value("${app.scraper.continuous.enabled:false}")
    private boolean continuousScrapingEnabled;

    @Value("${app.scraper.continuous.interval.minutes:30}")
    private int continuousIntervalMinutes;

    @Value("${app.scraper.continuous.max.pages:50}")
    private int maxContinuousPages;

    @Value("${app.scraper.continuous.products.threshold:10}")
    private int minProductsThreshold;

    private final java.util.Map<String, AtomicInteger> urlPageCounters = new java.util.concurrent.ConcurrentHashMap<>();

    private String generateJobName(String url) {
        String cleanUrl = url.replaceAll("https?://", "")
                .replaceAll("[^a-zA-Z0-9]", "_")
                .toLowerCase();
        return "scraping_" + cleanUrl;
    }

    private String generateContinuousJobName(String url) {
        return generateJobName(url) + "_continuous";
    }

    @PostConstruct
    public void init() {
        LocalDateTime lastRun = jobRepository.getLatestSuccessfulRunDate();
        if (lastRun == null || !lastRun.toLocalDate().equals(LocalDate.now())) {
            urls.forEach(url -> {
                String trimmedUrl = url.trim();
                String jobName = generateJobName(trimmedUrl);
                urlPageCounters.put(trimmedUrl, new AtomicInteger(isZaraUrl(trimmedUrl) ? zaraMaxScrolls : hmMaxPages));
                jobScheduler.enqueue(() -> runJobForUrl(trimmedUrl, jobName));
            });

            if (continuousScrapingEnabled) {
                scheduleContinuousScraping();
            }
        } else if (lastRun.toLocalDate().equals(LocalDate.now())) {
            for (int i = 0; i < 4; i++) {
                scraperCountdown.markScraperDone();
            }

            if (continuousScrapingEnabled) {
                urls.forEach(url -> {
                    String trimmedUrl = url.trim();
                    urlPageCounters.putIfAbsent(trimmedUrl, new AtomicInteger(isZaraUrl(trimmedUrl) ? zaraMaxScrolls : hmMaxPages));
                });
                scheduleContinuousScraping();
            }
        }
    }

    private void scheduleContinuousScraping() {
        System.out.println("Scheduling continuous scraping with " + continuousIntervalMinutes + " minute intervals");

        urls.forEach(url -> {
            String trimmedUrl = url.trim();
            String continuousJobName = generateContinuousJobName(trimmedUrl);

            jobScheduler.scheduleRecurrently(
                    continuousJobName,
                    Duration.ofMinutes(continuousIntervalMinutes),
                    () -> runContinuousScrapingForUrl(trimmedUrl, continuousJobName)
            );
        });
    }

    private JobEntity getOrCreateJob(String jobName) {
        Optional<JobEntity> existingJob = jobRepository.findByJobName(jobName);
        if (existingJob.isPresent()) {
            return existingJob.get();
        } else {
            JobEntity newJob = new JobEntity(jobName);
            return jobRepository.save(newJob);
        }
    }

    @Job(name = "scraping-job-for-url")
    public void runJobForUrl(String url, String jobName) {
        System.out.println("Starting initial scraping job for URL: " + url);
        JobEntity jobEntity = getOrCreateJob(jobName);
        jobEntity.startJob();
        jobRepository.save(jobEntity);

        try {
            String gender = detectGender(url);
            System.out.println("Processing URL: " + url + " | Gender: " + gender);

            List<ClothingDTO> scrapedProducts;

            if (isZaraUrl(url)) {
                System.out.println("Scraping Zara " + gender + " products...");
                scrapedProducts = zaraScrapingService.scrapeProducts(url, zaraMaxScrolls, gender);
                System.out.println("Scraped " + scrapedProducts.size() + " " + gender + " products from Zara");

            } else if (isHmUrl(url)) {
                System.out.println("Scraping H&M " + gender + " products...");
                scrapedProducts = hmScrapingService.scrapeProducts(url, hmMaxPages, gender);
                System.out.println("Scraped " + scrapedProducts.size() + " " + gender + " products from H&M");

            } else {
                System.out.println("Unknown URL format: " + url);
                return;
            }

            processScrapedProducts(scrapedProducts);
            jobEntity.completeJobSuccessfully();
            jobRepository.save(jobEntity);

        } catch (Exception e) {
            System.err.println("Error scraping URL: " + url + " - " + e.getMessage());
            jobEntity.failJob();
            jobRepository.save(jobEntity);
        }

        System.out.println("Initial scraping job completed for URL: " + url);
    }

    @Job(name = "continuous-scraping-job")
    public void runContinuousScrapingForUrl(String url, String jobName) {
        AtomicInteger currentPage = urlPageCounters.get(url);
            if (currentPage == null) {
            System.out.println("No page counter found for URL: " + url);
            return;
        }

        int nextPageStart = currentPage.get();
        if (nextPageStart >= maxContinuousPages) {
            System.out.println("Reached maximum pages (" + maxContinuousPages + ") for URL: " + url);
            return;
        }

        System.out.println("Starting continuous scraping for URL: " + url + " from page/scroll: " + nextPageStart);
        JobEntity jobEntity = getOrCreateJob(jobName);
        jobEntity.startJob();
        jobRepository.save(jobEntity);

        try {
            String gender = detectGender(url);
            List<ClothingDTO> scrapedProducts;

            int batchSize = isZaraUrl(url) ? 3 : 2;
            int endPage = Math.min(nextPageStart + batchSize, maxContinuousPages);

            if (isZaraUrl(url)) {
                System.out.println("Continuous scraping Zara " + gender + " products from scroll " + nextPageStart + " to " + endPage);
                scrapedProducts = zaraScrapingService.scrapeProductsWithRange(url, nextPageStart, endPage, gender);
            } else if (isHmUrl(url)) {
                System.out.println("Continuous scraping H&M " + gender + " products from page " + nextPageStart + " to " + endPage);
                scrapedProducts = hmScrapingService.scrapeProductsWithRange(url, nextPageStart, endPage, gender);
            } else {
                System.out.println("Unknown URL format: " + url);
                return;
            }

            System.out.println("Continuous scraping found " + scrapedProducts.size() + " products");
            if (scrapedProducts.size() < minProductsThreshold) {
                System.out.println("Found fewer than " + minProductsThreshold + " products. Stopping continuous scraping for: " + url);
                jobScheduler.delete(jobName);
                return;
            }

            processScrapedProducts(scrapedProducts);

            currentPage.set(endPage);

            jobEntity.completeJobSuccessfully();
            jobRepository.save(jobEntity);

            System.out.println("Continuous scraping completed for URL: " + url + ". Next start: " + endPage);

        } catch (Exception e) {
            System.err.println("Error in continuous scraping for URL: " + url + " - " + e.getMessage());
            jobEntity.failJob();
            jobRepository.save(jobEntity);
        }
    }

    public void triggerContinuousScrapingForUrl(String url) {
        if (!continuousScrapingEnabled) {
            System.out.println("Continuous scraping is disabled");
            return;
        }

        String trimmedUrl = url.trim();
        if (!urls.contains(trimmedUrl)) {
            System.out.println("URL not in configured list: " + trimmedUrl);
            return;
        }

        String continuousJobName = generateContinuousJobName(trimmedUrl);
        jobScheduler.enqueue(() -> runContinuousScrapingForUrl(trimmedUrl, continuousJobName));
    }

    public void stopContinuousScrapingForUrl(String url) {
        String continuousJobName = generateContinuousJobName(url.trim());
        jobScheduler.delete(continuousJobName);
        System.out.println("Stopped continuous scraping for URL: " + url);
    }

    public void resetPageCounterForUrl(String url) {
        String trimmedUrl = url.trim();
        AtomicInteger counter = urlPageCounters.get(trimmedUrl);
        if (counter != null) {
            int initialValue = isZaraUrl(trimmedUrl) ? zaraMaxScrolls : hmMaxPages;
            counter.set(initialValue);
            System.out.println("Reset page counter for " + url + " to " + initialValue);
        }
    }

    private String detectGender(String url) {
        String lowerUrl = url.toLowerCase();
        if (lowerUrl.contains("zara.com")) {
            if (lowerUrl.contains("/man") || lowerUrl.contains("/men") ||
                    lowerUrl.contains("man-all-products") || lowerUrl.contains("male")) {
                return "MALE";
            } else if (lowerUrl.contains("/woman") || lowerUrl.contains("/women") ||
                    lowerUrl.contains("woman-new-in") || lowerUrl.contains("female")) {
                return "FEMALE";
            }
        }
        if (lowerUrl.contains("hm.com")) {
            if (lowerUrl.contains("/men/") || lowerUrl.contains("/man/") ||
                    lowerUrl.contains("en_in/men") || lowerUrl.contains("male")) {
                return "MALE";
            } else if (lowerUrl.contains("/women/") || lowerUrl.contains("/woman/") ||
                    lowerUrl.contains("en_in/women") || lowerUrl.contains("female")) {
                return "FEMALE";
            }
        }

        System.out.println("Warning: Could not detect gender from URL: " + url + ". Defaulting to UNISEX");
        return "UNISEX";
    }

    private boolean isZaraUrl(String url) {
        return url.toLowerCase().contains("zara.com");
    }

    private boolean isHmUrl(String url) {
        return url.toLowerCase().contains("hm.com");
    }

    private void processScrapedProducts(List<ClothingDTO> products) {
        if (products.isEmpty()) {
            System.out.println("No products to process");
            return;
        }

        Set<String> scrapedProductIds = products.stream()
                .map(ClothingDTO::getProductId)
                .filter(id -> id != null && !id.trim().isEmpty())
                .collect(Collectors.toSet());

        if (scrapedProductIds.isEmpty()) {
            System.out.println("No valid product IDs found in scraped products");
            return;
        }

        Set<String> existingProductIds = swipeStyleRepo.findExistingProductIds(scrapedProductIds);
        System.out.println("Found " + existingProductIds.size() + " existing products in database");

        List<ClothingDTO> newProducts = products.stream()
                .filter(product -> product.getProductId() != null &&
                        !product.getProductId().trim().isEmpty() &&
                        !existingProductIds.contains(product.getProductId()))
                .toList();

        System.out.println("Found " + newProducts.size() + " new products to save");
        System.out.println("Skipped " + (products.size() - newProducts.size()) + " duplicate products");

        if (!newProducts.isEmpty()) {
            List<Clothing> clothingEntities = newProducts.stream()
                    .map(ClothingMapper::toEntity)
                    .collect(Collectors.toList());

            clothingService.saveAllClothing(clothingEntities);
            System.out.println("Successfully saved " + newProducts.size() + " new products");
            scraperCountdown.markScraperDone();
        } else {
            System.out.println("No new products to save - all were duplicates");
        }
    }
}