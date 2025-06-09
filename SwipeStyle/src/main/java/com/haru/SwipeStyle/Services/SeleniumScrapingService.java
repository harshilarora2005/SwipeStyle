package com.haru.SwipeStyle.Services;

import com.haru.SwipeStyle.model.ZaraProduct;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SeleniumScrapingService {

    @Autowired
    private ApplicationContext applicationContext;

    private WebDriver getWebDriver() {
        return applicationContext.getBean(WebDriver.class);
    }

    public List<ZaraProduct> scrapeZaraProducts(String categoryUrl, int maxScrolls) {
        WebDriver driver = getWebDriver();
        List<ZaraProduct> products = new ArrayList<>();
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
                        ZaraProduct product = extractProductData(productElement, scroll + 1);
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

    private ZaraProduct extractProductData(WebElement productElement, int pageNumber) {
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

            // Extract price
            String price = "";
            try {
                WebElement priceElement = productElement.findElement(
                        By.cssSelector(".price-current__amount .money-amount__main")
                );
                price = priceElement.getText().trim();
            } catch (NoSuchElementException e) {
                price = "Price not available";
            }

            // Extract image URL
            String imageUrl = "";
            try {
                WebElement imageElement = productElement.findElement(
                        By.cssSelector("img.media-image__image")
                );
                imageUrl = imageElement.getAttribute("src");
            } catch (NoSuchElementException e) {
                imageUrl = "";
            }

            // Extract product URL
            String productUrl = "";
            try {
                WebElement linkElement = productElement.findElement(
                        By.cssSelector("a.product-link")
                );
                productUrl = linkElement.getAttribute("href");
            } catch (NoSuchElementException e) {
                productUrl = "";
            }

            // Extract color information
            String color = "";
            try {
                WebElement colorElement = productElement.findElement(
                        By.cssSelector(".product-grid-product-info-colors__bubble")
                );
                color = colorElement.getAttribute("aria-label");
            } catch (NoSuchElementException e) {
                color = "Color not specified";
            }

            // Extract alt text from image
            String altText = "";
            try {
                WebElement imageElement = productElement.findElement(
                        By.cssSelector("img.media-image__image")
                );
                altText = imageElement.getAttribute("alt");
            } catch (NoSuchElementException e) {
                altText = "";
            }

            // Create and return product object
            if (productId != null && !productId.isEmpty()) {
                return new ZaraProduct(productId, name, price, imageUrl, productUrl, color, altText, pageNumber);
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

                }
            }
        } catch (Exception e) {
            
        }
    }

    public ZaraProduct scrapeProductDetails(String productUrl) {
        WebDriver driver = getWebDriver();

        try {
            driver.get(productUrl);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Wait for product details to load
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector(".product-detail-info")
            ));

            // Extract detailed product information
            String name = driver.findElement(By.cssSelector(".product-detail-info__header-name")).getText();
            String price = driver.findElement(By.cssSelector(".price-current__amount")).getText();

            // Extract product ID from URL
            Pattern pattern = Pattern.compile("p(\\d+)\\.html");
            Matcher matcher = pattern.matcher(productUrl);
            String productId = matcher.find() ? matcher.group(1) : "";

            // Get main product image
            String imageUrl = "";
            try {
                WebElement mainImage = driver.findElement(By.cssSelector(".product-detail-images img"));
                imageUrl = mainImage.getAttribute("src");
            } catch (NoSuchElementException e) {
                imageUrl = "";
            }

            return new ZaraProduct(productId, name, price, imageUrl, productUrl, "", "", 1);

        } catch (Exception e) {
            throw new RuntimeException("Error scraping product details: " + e.getMessage(), e);
        } finally {
            driver.quit();
        }
    }

    public List<ZaraProduct> searchZaraProducts(String searchQuery, int maxResults) {
        WebDriver driver = getWebDriver();
        List<ZaraProduct> searchResults = new ArrayList<>();

        try {
            driver.get("https://www.zara.com/in/en/");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Find and click search button
            WebElement searchButton = wait.until(
                    ExpectedConditions.elementToBeClickable(By.cssSelector("[data-qa-action='search-open']"))
            );
            searchButton.click();

            // Enter search query
            WebElement searchInput = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.cssSelector("input[data-qa-qualifier='search-term']"))
            );
            searchInput.clear();
            searchInput.sendKeys(searchQuery);
            searchInput.sendKeys(Keys.ENTER);

            // Wait for search results
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("li.product-grid-product")
            ));

            // Extract search results
            List<WebElement> productElements = driver.findElements(
                    By.cssSelector("li.product-grid-product")
            );

            int count = 0;
            for (WebElement productElement : productElements) {
                if (count >= maxResults) break;

                ZaraProduct product = extractProductData(productElement, 1);
                if (product != null) {
                    searchResults.add(product);
                    count++;
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Error searching Zara products: " + e.getMessage(), e);
        } finally {
            driver.quit();
        }

        return searchResults;
    }
}