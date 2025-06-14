package com.haru.SwipeStyle.Services.impl;

import com.haru.SwipeStyle.DTOs.ClothingDTO;
import com.haru.SwipeStyle.Services.SwipeStyleService;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.*;

@Service
public class ZaraScrapingService implements SwipeStyleService {

    @Autowired
    private ApplicationContext applicationContext;

    private WebDriver getWebDriver() {
        return applicationContext.getBean(WebDriver.class);
    }

    @Override
    public List<ClothingDTO> scrapeProducts(String categoryUrl, int maxScrolls, String gender) {
        WebDriver driver = getWebDriver();
        List<ClothingDTO> products = new ArrayList<>();
        Set<String> seenProductIds = new HashSet<>();
        Map<String, ClothingDTO> productMap = new HashMap<>();

        try {
            driver.get(categoryUrl);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("li.product-grid-product")
            ));

            try {
                WebElement button = wait.until(ExpectedConditions.elementToBeClickable(
                        By.cssSelector("button[aria-label='Switch to zoom 2']")
                ));
                button.click();
                System.out.println("Button found and clicked");
            } catch (NoSuchElementException e) {
                System.out.println("Button not found");
            }

            handleCookieConsent(driver, wait);

            for (int scroll = 0; scroll < maxScrolls; scroll++) {
                int productsAddedThisScroll = 0;
                List<WebElement> productElements = driver.findElements(
                        By.cssSelector("li.product-grid-product")
                );

                System.out.println("Found " + productElements.size() + " products on scroll " + (scroll + 1));


                for (WebElement productElement : productElements) {

                    try {
//                        if (productsAddedThisScroll >= 8) {
//                            break;
//                        }
                        ClothingDTO product = extractProductData(productElement, gender);
                        if (product == null || product.getProductId() == null) {
                            continue;
                        }
                        ClothingDTO existingProduct = productMap.get(product.getProductId());
                        if (existingProduct == null){
                            // New product - add if image is valid
                            if (isValidImage(product.getImageUrl())) {
                                products.add(product);
                                productMap.put(product.getProductId(), product);
                                productsAddedThisScroll++;
                            }
                        } else {
                            // Existing product - update if we now have a better image
                            if (isValidImage(product.getImageUrl()) &&
                                    !isValidImage(existingProduct.getImageUrl())) {
                                // Update the existing product with better image
                                existingProduct.setImageUrl(product.getImageUrl());
                                existingProduct.setProductUrl(product.getProductUrl());
                                // You might want to update other fields here as well
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Error extracting product data: " + e.getMessage());
                    }
                }

                if (scroll < maxScrolls - 1) {
                    scrollToLoadMore(driver);
                    Thread.sleep(5000);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Error scraping Zara products: " + e.getMessage(), e);
        } finally {
            driver.quit();
        }

        return products;
    }
    private boolean isValidImage(String imageUrl) {
        return imageUrl != null &&
                !imageUrl.isEmpty() &&
                !imageUrl.contains("transparent") &&
                !imageUrl.contains("placeholder");
    }
    private ClothingDTO extractProductData(WebElement productElement, String gender) {
        try {
            String productId = extractProductId(productElement);
            String imageUrl = extractImageUrl(productElement);
            String productUrl = extractProductUrl(productElement);
            String altText = extractAltText(productElement);
            String productName = extractProductName(productElement);
            String productPrice = extractProductPrice(productElement);

            if (productId != null && !productId.isEmpty()) {
                return new ClothingDTO(productId, productName, gender, productPrice, imageUrl, productUrl, altText);
            }
        } catch (Exception e) {
            System.err.println("Error extracting individual product: " + e.getMessage());
        }
        return null;
    }

    private String extractProductId(WebElement productElement) {
        return productElement.getAttribute("data-productid");
    }

    private String extractImageUrl(WebElement productElement) {
        try {
            WebElement imageElement = productElement.findElement(By.cssSelector("img.media-image__image"));

            String src = imageElement.getAttribute("src");
            return (src == null || src.isEmpty()) ? imageElement.getAttribute("data-src") : src;
        } catch (NoSuchElementException e) {
            return "";
        }
    }

    private String extractProductUrl(WebElement productElement) {
        try {
            WebElement linkElement = productElement.findElement(By.cssSelector("a.product-link"));
            return linkElement.getAttribute("href");
        } catch (NoSuchElementException e) {
            return "";
        }
    }

    private String extractAltText(WebElement productElement) {
        try {
            WebElement imageElement = productElement.findElement(By.cssSelector("img.media-image__image"));
            return imageElement.getAttribute("alt");
        } catch (NoSuchElementException e) {
            return "";
        }
    }

    private String extractProductName(WebElement productElement) {
        try {
            WebElement nameElement = productElement.findElement(By.cssSelector(".product-grid-product-info__name h3"));
            return nameElement.getText().trim();
        } catch (NoSuchElementException e) {
            return "Unknown Product";
        }
    }

    private String extractProductPrice(WebElement productElement) {
        try {
            WebElement priceElement = productElement.findElement(By.cssSelector(".price-current__amount .money-amount__main"));
            return priceElement.getText().trim();
        } catch (NoSuchElementException e) {
            return "No price found";
        }
    }

    private void scrollToLoadMore(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        for (int i = 0; i < 3; i++) {
            js.executeScript("window.scrollBy(0, 500);");
            try {
                Thread.sleep(300); // Short pause to allow loading
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void handleCookieConsent(WebDriver driver, WebDriverWait wait) {
        try {

            List<String> cookieSelectors = Arrays.asList(
                    "#onetrust-accept-btn-handler",
                    ".cookie-accept",
                    "[data-qa-action='accept-cookies']",
                    ".accept-cookies",
                    "#acceptCookies"
            );

            for (String selector : cookieSelectors) {
                try {
                    WebElement cookieButton = wait.until(
                            ExpectedConditions.elementToBeClickable(By.cssSelector(selector))
                    );
                    cookieButton.click();
                    Thread.sleep(1000);
                    return;
                } catch (Exception e) {
                    //
                }
            }
        } catch (Exception e) {
            //
        }
    }
}