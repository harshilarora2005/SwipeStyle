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
    String gender;

    @Override
    public List<ClothingDTO> scrapeProducts(String categoryUrl, int maxScrolls,String gender) {
        WebDriver driver = getWebDriver();
        List<ClothingDTO> products = new ArrayList<>();
        Set<String> seenProductIds = new HashSet<>();
        this.gender = gender;
        try {
            driver.get(categoryUrl);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("li.product-grid-product")
            ));


            handleCookieConsent(driver, wait);

            for (int scroll = 0; scroll < maxScrolls; scroll++) {

                List<WebElement> productElements = driver.findElements(
                        By.cssSelector("li.product-grid-product")
                );

                System.out.println("Found " + productElements.size() + " products on scroll " + (scroll + 1));


                for (WebElement productElement : productElements) {
                    try {
                        ClothingDTO product = extractProductData(productElement);
                        if (product != null && !seenProductIds.contains(product.getProductId())) {
                            products.add(product);
                            seenProductIds.add(product.getProductId());
                        }
                    } catch (Exception e) {
                        System.err.println("Error extracting product data: " + e.getMessage());
                    }
                }

                if (scroll < maxScrolls - 1) {
                    scrollToLoadMore(driver);
                    Thread.sleep(500);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Error scraping Zara products: " + e.getMessage(), e);
        } finally {
            driver.quit();
        }

        return products;
    }

    private ClothingDTO extractProductData(WebElement productElement) {
        try {
            String productId = productElement.getAttribute("data-productid");
            String imageUrl = "";
            try {
                WebElement imageElement = productElement.findElement(
                        By.cssSelector("img.media-image__image")
                );
                imageUrl = imageElement.getAttribute("src");
                if (imageUrl == null || imageUrl.isEmpty()) {
                    imageUrl = imageElement.getAttribute("data-src");
                }
            } catch (NoSuchElementException e) {
                imageUrl = "";
            }

            String productUrl = "";
            try {
                WebElement linkElement = productElement.findElement(
                        By.cssSelector("a.product-link")
                );
                productUrl = linkElement.getAttribute("href");
            } catch (NoSuchElementException e) {
                productUrl = "";
            }

            String altText = "";
            try {
                WebElement imageElement = productElement.findElement(
                        By.cssSelector("img.media-image__image")
                );
                altText = imageElement.getAttribute("alt");
            } catch (NoSuchElementException e) {
                altText = "";
            }
            List<String> details = getProductDetailsFromPage(productUrl);
            if (productId != null && !productId.isEmpty()) {
                return new ClothingDTO(productId, details.getFirst(), gender, details.getLast(), imageUrl,productUrl,altText);
            }

        } catch (Exception e) {
            System.err.println("Error extracting individual product: " + e.getMessage());
        }

        return null;
    }
    private List<String> getProductDetailsFromPage(String productUrl) {
        WebDriver detailDriver = getWebDriver();
        WebDriverWait detailWait = new WebDriverWait(detailDriver, Duration.ofSeconds(10));
        List<String> details = new ArrayList<>();
        try {
            detailDriver.get(productUrl);
            detailWait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
            Thread.sleep(1000);
            String name = getProductName(detailDriver);
            String price = getProductPrice(detailDriver);
            details.add(name);
            details.add(price);
        } catch (Exception e) {
            details.add("Name not found");
            details.add("Price not found");
        } finally {
            detailDriver.quit();
        }
        return details;
    }

    private void scrollToLoadMore(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");

        js.executeScript("window.scrollBy(0, window.innerHeight);");
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
    private String getProductPrice(WebDriver element) {
        String price = "";
        try{
            WebElement priceElement = element.findElement(
                    By.cssSelector(".price-current__amount .money-amount__main")
            );
            price = priceElement.getText().trim();
        }catch (NoSuchElementException e){
            price = "No price found";
        }
        return price;
    }
    private String getProductName(WebDriver element) {
        String productName = "";
        try{
            WebElement productNameElement = element.findElement(
                    By.cssSelector(".product-detail-info__header-name")
            );
            productName = productNameElement.getText().trim();
        }catch (NoSuchElementException e){
            productName = "Unknown Product";
        }
        return productName;
    }

}