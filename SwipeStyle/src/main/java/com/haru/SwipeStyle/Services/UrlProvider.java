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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UrlProvider {
    @Autowired
    JobScheduler jobScheduler;

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

    private String generateJobName(String url) {
        String cleanUrl = url.replaceAll("https?://", "")
                .replaceAll("[^a-zA-Z0-9]", "_")
                .toLowerCase();
        return "scraping_" + cleanUrl;
    }

    @PostConstruct
    public void init() {
        LocalDateTime lastRun = jobRepository.getLatestSuccessfulRunDate();
        if (lastRun == null || !lastRun.toLocalDate().equals(LocalDate.now())) {
            urls.forEach(url -> {
                String trimmedUrl = url.trim();
                String jobName = generateJobName(trimmedUrl);
                jobScheduler.enqueue(() -> runJobForUrl(trimmedUrl,jobName));
            });
        }
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
        System.out.println("Starting scraping job for URL: " + url);
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

        System.out.println("Scraping job completed for URL: " + url);
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

        // Extract product IDs from scraped products
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

            swipeStyleRepo.saveAll(clothingEntities);
            System.out.println("Successfully saved " + newProducts.size() + " new products");
            scraperCountdown.markScraperDone();
        } else {
            System.out.println("No new products to save - all were duplicates");
        }
    }
}