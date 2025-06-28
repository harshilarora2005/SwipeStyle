package com.haru.SwipeStyle.Controllers;

import com.haru.SwipeStyle.Components.ScraperCountdown;
import com.haru.SwipeStyle.DTOs.ClothingDTO;
import com.haru.SwipeStyle.Entities.Clothing;
import com.haru.SwipeStyle.Entities.RecommendationRequest;
import com.haru.SwipeStyle.Mapper.ClothingMapper;
import com.haru.SwipeStyle.Services.ClothingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
@RestController
@RequestMapping("/api/swipe-style")
@Tag(name = "SwipeStyle API", description = "Endpoints for clothing retrieval and recommendations")
public class SwipeStyleController {

    @Autowired
    private ScraperCountdown scraperCountdown;

    @Autowired
    private ClothingService clothingService;

    @GetMapping("/products")
    @Operation(
            summary = "Get paginated clothing products",
            description = "Returns a paginated list of clothing items. If scraping is still in progress, responds with HTTP 202."
    )
    public ResponseEntity<?> getProducts(
            @RequestParam(defaultValue = "0") int page) {

        if (!scraperCountdown.isCompleted()) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "loading");
            response.put("message", "Scraping is in progress. Please wait.");
            return ResponseEntity.status(202).body(response);
        }

        Pageable pageable = PageRequest.of(page, 20);
        List<Clothing> clothingList = clothingService.getAllClothingItemsList(pageable);
        List<ClothingDTO> clothingDTOs = clothingList.stream()
                .map(ClothingMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(clothingDTOs);
    }

    @GetMapping("/products/{gender}")
    @Operation(
            summary = "Get products filtered by gender",
            description = "Returns clothing items for a specified gender (e.g., MALE, FEMALE, UNISEX). If scraping is in progress, responds with HTTP 202."
    )
    public ResponseEntity<?> getProductsByGender(@PathVariable String gender) {
        try {
            if (!scraperCountdown.isCompleted()) {
                Map<String, String> response = new HashMap<>();
                response.put("status", "loading");
                return ResponseEntity.status(202).body(response);
            }
            System.out.println("Fetching products for gender: " + gender);
            List<ClothingDTO> products;
            if (gender.equals("UNISEX")) {
                products = clothingService.getAllProductsAsDTO();
            } else {
                products = clothingService.getProductsByGender(gender.toUpperCase());
            }
            return products.isEmpty()
                    ? ResponseEntity.noContent().build()
                    : ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching products: " + e.getMessage());
        }
    }

    @GetMapping("/get-clothing-id")
    @Operation(
            summary = "Get clothing entity ID from productId",
            description = "Returns the internal database ID for a given productId."
    )
    public ResponseEntity<?> getClothingId(@RequestParam String productId) {
        try {
            Long clothingId = clothingService.getId(productId);
            return ResponseEntity.ok(clothingId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to get clothing ID for productId: " + productId);
        }
    }

    @PostMapping("/recommend")
    @Operation(
            summary = "Get clothing recommendations based on liked items",
            description = "Returns a list of recommended clothing items given a set of liked items and previously recommended products."
    )
    public List<ClothingDTO> recommendFromLikedItems(@RequestBody RecommendationRequest request) {
        List<Clothing> recommendations = clothingService.recommendBasedOnLikedItems(
                request.getLikedItems(),
                request.getPreviouslyRecommended() != null ? request.getPreviouslyRecommended() : new HashSet<>(),
                5
        );
        return recommendations.stream()
                .map(ClothingMapper::toDTO)
                .collect(Collectors.toList());
    }
}
