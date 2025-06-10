package com.haru.SwipeStyle.Services;

import com.haru.SwipeStyle.DTOs.ClothingDTO;
import com.haru.SwipeStyle.model.ZaraProduct;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;


@Service
public class ZaraScrapingService {

    @Autowired
    private ApplicationContext applicationContext;

    private WebDriver getWebDriver() {
        return applicationContext.getBean(WebDriver.class);
    }

    public List<ClothingDTO> scrapeZaraProducts(String categoryUrl, int maxScrolls) {
        WebDriver driver = getWebDriver();
        List<ClothingDTO> products = new ArrayList<>();
        Set<String> seenProductIds = new HashSet<>();

        try {
            driver.get(categoryUrl);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

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
                        ClothingDTO product = extractProductData(productElement, scroll + 1);
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
                    Thread.sleep(3000);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Error scraping Zara products: " + e.getMessage(), e);
        } finally {
            driver.quit();
        }

        return products;
    }

    private ClothingDTO extractProductData(WebElement productElement, int pageNumber) {
        try {
            String productId = productElement.getAttribute("data-productid");

            String name = "";
            try {
                WebElement nameElement = productElement.findElement(
                        By.cssSelector("h3")
                );
                name = nameElement.getText().trim();
            } catch (NoSuchElementException e) {

                try {
                    WebElement nameElement = productElement.findElement(
                            By.cssSelector(".product-grid-product-info__main-info a")
                    );
                    name = nameElement.getText().trim();
                } catch (NoSuchElementException ex) {
                    name = "Unknown Product";
                }
            }
            String price="";
            try {
                WebElement priceElement = productElement.findElement(
                        By.cssSelector(".price-current__amount .money-amount__main")
                );
                price = priceElement.getText().trim();
            } catch (NoSuchElementException e) {
                price = "No price found";
            }
            String imageUrl = "";
            try {
                WebElement imageElement = productElement.findElement(
                        By.cssSelector("img.media-image__image")
                );
                imageUrl = imageElement.getAttribute("src");
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

            if (productId != null && !productId.isEmpty()) {
                return new ClothingDTO(productId,name,price,imageUrl,productUrl,altText);
            }

        } catch (Exception e) {
            System.err.println("Error extracting individual product: " + e.getMessage());
        }

        return null;
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

}