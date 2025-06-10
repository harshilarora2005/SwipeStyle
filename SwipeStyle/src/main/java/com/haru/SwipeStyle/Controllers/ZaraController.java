package com.haru.SwipeStyle.Controllers;
import com.haru.SwipeStyle.DTOs.ClothingDTO;
import com.haru.SwipeStyle.model.ZaraProduct;
import com.haru.SwipeStyle.Services.ZaraScrapingService;
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
    private ZaraScrapingService zaraService;

    @GetMapping("/scrape")
    public ResponseEntity<?> scrapeZaraProducts(
            @RequestParam String categoryUrl,
            @RequestParam(defaultValue = "3") int maxScrolls) {
        try {
            List<ClothingDTO> products = zaraService.scrapeZaraProducts(categoryUrl, maxScrolls);

            return ResponseEntity.ok(Map.of(
                    "count", products.size(),
                    "products", products
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}