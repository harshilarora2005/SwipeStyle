package com.haru.SwipeStyle.Services.impl;


import com.haru.SwipeStyle.DTOs.ClothingDTO;
import com.haru.SwipeStyle.Services.SwipeStyleService;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.*;

@Service
public class HMScrapingService implements SwipeStyleService {

    @Autowired
    private ApplicationContext applicationContext;

    private static final String DOMAIN = "https://image.hm.com/";

    private WebDriver getWebDriver() {
        return applicationContext.getBean(WebDriver.class);
    }

    @Override
    public List<ClothingDTO> scrapeProducts(String baseCategoryUrl, int maxPages,String gender) {
        WebDriver driver = getWebDriver();
        List<ClothingDTO> products = new ArrayList<>();
        Set<String> seenProductIds = new HashSet<>();
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            handleCookieConsent(driver, wait);

            for (int page = 1; page <= maxPages; page++) {
                String pageUrl = baseCategoryUrl;
                if (baseCategoryUrl.contains("?")) {
                    pageUrl += "&page=" + page;
                } else {
                    pageUrl += "?page=" + page;
                }

                driver.get(pageUrl);

                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("li > div[data-hydration-on-demand]")));

                List<WebElement> productElements = driver.findElements(By.cssSelector("li > div[data-hydration-on-demand] article"));

                System.out.println("Page " + page + " found " + productElements.size() + " products");

                for (WebElement productElement : productElements) {
                    try {
                        ClothingDTO product = extractProductData(productElement,gender);
                        if (product != null && !seenProductIds.contains(product.getProductId())) {
                            products.add(product);
                            seenProductIds.add(product.getProductId());
                        }
                    } catch (Exception e) {
                        System.err.println("Error extracting product data on page " + page + ": " + e.getMessage());
                    }
                }

                Thread.sleep(500);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error scraping H&M products: " + e.getMessage(), e);
        } finally {
            driver.quit();
        }

        return products;
    }

    @Override
    public List<ClothingDTO> scrapeProductsWithRange(String url, int startPage, int endPage, String gender) {
        WebDriver driver = getWebDriver();
        List<ClothingDTO> products = new ArrayList<>();
        Set<String> seenProductIds = new HashSet<>();
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            handleCookieConsent(driver, wait);

            for (int page = startPage; page <= endPage; page++) {
                String pageUrl = url;
                if (url.contains("?")) {
                    pageUrl += "&page=" + page;
                } else {
                    pageUrl += "?page=" + page;
                }

                driver.get(pageUrl);

                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("li > div[data-hydration-on-demand]")));

                List<WebElement> productElements = driver.findElements(By.cssSelector("li > div[data-hydration-on-demand] article"));

                System.out.println("Page " + page + " found " + productElements.size() + " products");

                for (WebElement productElement : productElements) {
                    try {
                        ClothingDTO product = extractProductData(productElement,gender);
                        if (product != null && !seenProductIds.contains(product.getProductId())) {
                            products.add(product);
                            seenProductIds.add(product.getProductId());
                        }
                    } catch (Exception e) {
                        System.err.println("Error extracting product data on page " + page + ": " + e.getMessage());
                    }
                }

                Thread.sleep(500);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error scraping H&M products: " + e.getMessage(), e);
        } finally {
            driver.quit();
        }

        return products;
    }

    private ClothingDTO extractProductData(WebElement productElement,String gender) {
        try {
            String productId = productElement.getAttribute("data-articlecode");
            if (productId == null || productId.isEmpty()) return null;

            WebElement linkElement = productElement.findElement(By.xpath(".//a[h2]"));
            String productUrl = linkElement.getAttribute("href");

            String name = "";
            try {
                WebElement nameElement = linkElement.findElement(By.xpath(".//h2"));
                name = nameElement.getText();
            } catch (NoSuchElementException e) {
                name = "Unknown";
            }
            String priceText = "";
            try {
                WebElement priceElement = productElement.findElement(By.xpath(".//p//span[contains(text(), 'Rs.') or contains(text(), '₹')]"));
                priceText = priceElement.getText().replaceAll("[^0-9.]", "");
            } catch (NoSuchElementException e) {
                priceText = "0";
            }
            String price = priceText.isEmpty() ? "Price Not Found" : priceText;
            if (price.startsWith(".")) {
                price = price.substring(1);
            }
            WebElement imgElement = productElement.findElement(By.cssSelector("img[data-src]"));
            String imgSrc = imgElement.getAttribute("data-src");
            String altImage = imgElement.getAttribute("data-altimage");

            if (altImage != null && !altImage.startsWith("http")) {
                altImage = DOMAIN +  altImage;
            }

            String imageUrl = (altImage != null && !altImage.isEmpty()) ? altImage : imgSrc;
            if (imageUrl != null && !imageUrl.startsWith("http")) {
                imageUrl = DOMAIN +  imageUrl;
            }

            String altText = imgElement.getAttribute("alt");

            return new ClothingDTO(productId, name, gender, price, imageUrl, productUrl, altText);

        } catch (Exception e) {
            System.err.println("Error extracting individual product: " + e.getMessage());
            return null;
        }
    }

    private void handleCookieConsent(WebDriver driver, WebDriverWait wait) {
        List<String> cookieSelectors = Arrays.asList(
                "#onetrust-accept-btn-handler",
                ".cookie-accept",
                "[data-qa-action='accept-cookies']",
                ".accept-cookies",
                "#acceptCookies"
        );
        for (String selector : cookieSelectors) {
            try {
                WebElement cookieButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(selector)));
                cookieButton.click();
                Thread.sleep(1000);
                return;
            } catch (Exception ignored) {}
        }
    }
}
