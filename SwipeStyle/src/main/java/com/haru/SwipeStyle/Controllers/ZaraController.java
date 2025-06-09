package com.haru.SwipeStyle.Controllers;
import com.haru.SwipeStyle.model.ZaraProduct;
import com.haru.SwipeStyle.Services.SeleniumScrapingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/zara")
@CrossOrigin(origins = "*")
public class ZaraController {

    @Autowired
    private SeleniumScrapingService zaraService;

    @GetMapping("/scrape")
    public ResponseEntity<?> scrapeZaraProducts(
            @RequestParam String categoryUrl,
            @RequestParam(defaultValue = "3") int maxScrolls) {
        try {
            List<ZaraProduct> products = zaraService.scrapeZaraProducts(categoryUrl, maxScrolls);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "count", products.size(),
                    "products", products
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/product-details")
    public ResponseEntity<?> getProductDetails(@RequestParam String productUrl) {
        try {
            ZaraProduct product = zaraService.scrapeProductDetails(productUrl);
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(
            @RequestParam String query,
            @RequestParam(defaultValue = "20") int maxResults) {
        try {
            List<ZaraProduct> products = zaraService.searchZaraProducts(query, maxResults);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "count", products.size(),
                    "products", products
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }
}