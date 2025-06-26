package com.haru.SwipeStyle.Controllers;

import com.haru.SwipeStyle.Components.ScraperCountdown;
import com.haru.SwipeStyle.DTOs.ClothingDTO;
import com.haru.SwipeStyle.Entities.Clothing;
import com.haru.SwipeStyle.Mapper.ClothingMapper;
import com.haru.SwipeStyle.Services.ClothingService;
import com.haru.SwipeStyle.Services.UserClothingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
@RestController
@RequestMapping("/api/swipe-style")
public class SwipeStyleController {

    @Autowired
    private ScraperCountdown scraperCountdown;

    @Autowired
    private ClothingService clothingService;

    @Autowired
    private UserClothingService userClothingService;

    @GetMapping("/products")
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
    public ResponseEntity<?> getProductsByGender(@PathVariable String gender) {
        try {
            if (!scraperCountdown.isCompleted()) {
                Map<String, String> response = new HashMap<>();
                response.put("status", "loading");
                return ResponseEntity.status(202).body(response);
            }
            System.out.println("Fetching products for gender: " + gender);
            List<ClothingDTO> products;
            if(gender.equals("UNISEX")) {
                products = clothingService.getAllProductsAsDTO();
                System.out.println("Total products in database: " + products.size());
            }else{
                products = clothingService.getProductsByGender(gender.toUpperCase());
            }
            System.out.println("Products found: " + products.size());
            if (products.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            if (!products.isEmpty()) {
                ClothingDTO firstProduct = products.get(0);
                System.out.println("First product details:");
                System.out.println("ProductId: " + firstProduct.getProductId());
                System.out.println("Name: " + firstProduct.getName());
                System.out.println("Price: " + firstProduct.getPrice());
                System.out.println("Gender: " + firstProduct.getGender());
                System.out.println("ImageUrl: " + firstProduct.getImageUrl());
            }
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching products: " + e.getMessage());
        }
    }

    @GetMapping("/get-clothing-id")
    public ResponseEntity<?> getClothingId(@RequestParam String productId) {
        try {
            Long clothingId = clothingService.getId(productId);
            System.out.println(clothingId);
            return ResponseEntity.ok(clothingId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to get clothing ID for productId: " + productId);
        }
    }

    @PostMapping("/recommend")
    public List<ClothingDTO> recommendFromLikedItems(@RequestBody List<ClothingDTO> likedItem) {
        List<Clothing> recommendations = clothingService.recommendBasedOnLikedItems(likedItem, 5);
        return recommendations.stream()
                .map(ClothingMapper::toDTO)
                .collect(Collectors.toList());
    }

}