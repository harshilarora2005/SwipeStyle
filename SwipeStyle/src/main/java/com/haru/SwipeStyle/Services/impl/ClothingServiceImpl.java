package com.haru.SwipeStyle.Services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haru.SwipeStyle.DTOs.ClothingDTO;
import com.haru.SwipeStyle.Entities.Category;
import com.haru.SwipeStyle.Entities.Clothing;
import com.haru.SwipeStyle.Mapper.ClothingMapper;
import com.haru.SwipeStyle.Repository.SwipeStyleRepo;
import com.haru.SwipeStyle.Services.ClothingService;
import com.haru.SwipeStyle.Services.EmbeddingService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.haru.SwipeStyle.Utils.ConsineSimilarity.averageVectors;
import static com.haru.SwipeStyle.Utils.ConsineSimilarity.cosineSimilarity;

@Service
@Transactional
public class ClothingServiceImpl implements ClothingService {

    private final SwipeStyleRepo swipeStyleRepo;

    private final EmbeddingService embeddingService;

    @Autowired
    private ObjectMapper objectMapper;

    public Category classifyCategory(String altText) {
        if (altText == null) return Category.MISC;
        altText = altText.toLowerCase();

        if (altText.contains("shirt") || altText.contains("t-shirt") || altText.contains("blouse") || altText.contains("tank") || altText.contains("sweater") || altText.contains("jacket") || altText.contains("coat") || altText.contains("hoodie") || altText.contains("blazer") || altText.contains("top"))
            return Category.TOP;

        if (altText.contains("jeans") || altText.contains("trousers") || altText.contains("shorts") || altText.contains("skirt") || altText.contains("pants"))
            return Category.BOTTOM;

        if (altText.contains("sneakers") || altText.contains("shoes") || altText.contains("boots") || altText.contains("sandals") || altText.contains("heels"))
            return Category.FOOTWEAR;

        if (altText.contains("hat") || altText.contains("scarf") || altText.contains("belt") || altText.contains("watch") || altText.contains("bag"))
            return Category.ACCESSORY;

        return Category.MISC;
    }


    @Autowired
    public ClothingServiceImpl(SwipeStyleRepo swipeStyleRepo) {
        this.swipeStyleRepo = swipeStyleRepo;
        this.embeddingService = new EmbeddingServiceImpl();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Clothing> getAllClothingItems() {
        return swipeStyleRepo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Clothing> getClothingById(Long id) {
        return swipeStyleRepo.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClothingDTO> getProductsByGender(String gender) {
        if (gender == null || gender.trim().isEmpty()) {
            throw new IllegalArgumentException("Gender cannot be null or empty");
        }
        return swipeStyleRepo.getProductIdsByGender(gender.toUpperCase());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Clothing> getAllClothingItemsList(Pageable pageable) {
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }
        return swipeStyleRepo.findAll(pageable).getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClothingDTO> getAllProductsAsDTO() {
        List<Clothing> allClothing = swipeStyleRepo.findAll();
        return allClothing.stream()
                .map(ClothingMapper::toDTO)
                .toList();
    }

    @Override
    public Clothing saveClothing(Clothing clothing) {
        if (clothing == null) {
            throw new IllegalArgumentException("Clothing cannot be null");
        }
        clothing.setCategory(classifyCategory(clothing.getAltText()));
        return swipeStyleRepo.save(clothing);
    }

    @Override
    public void saveAllClothing(List<Clothing> clothingList) {
        if (clothingList == null || clothingList.isEmpty()) {
            throw new IllegalArgumentException("Clothing list cannot be null or empty");
        }
        for (Clothing clothing : clothingList) {
            clothing.setCategory(classifyCategory(clothing.getAltText()));
        }
        swipeStyleRepo.saveAll(clothingList);
    }

    @Override
    public void deleteClothingById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (!swipeStyleRepo.existsById(id)) {
            throw new RuntimeException("Clothing with ID " + id + " not found");
        }
        swipeStyleRepo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        if (id == null) {
            return false;
        }
        return swipeStyleRepo.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public LocalDateTime getMaxCreatedAt() {
        return swipeStyleRepo.getMaxCreatedAt();
    }

    @Override
    @Transactional(readOnly = true)
    public Set<String> findExistingProductIds(Set<String> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            throw new IllegalArgumentException("Product IDs set cannot be null or empty");
        }
        return swipeStyleRepo.findExistingProductIds(productIds);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalCount() {
        return swipeStyleRepo.count();
    }

    @Override
    public long getId(String productId) {
        return swipeStyleRepo.findIdByProductId(productId);
    }

    @Override
    public List<Clothing> recommendBasedOnLikedItems(List<ClothingDTO> likedItems,
                                                     Set<String> previouslyRecommended,
                                                     int topN) {
        if (likedItems == null || likedItems.isEmpty()) {
            return List.of();
        }

        Set<String> likedItemIds = likedItems.stream()
                .map(ClothingDTO::getProductId)
                .collect(Collectors.toSet());

        List<List<Double>> likedVectors = likedItems.stream()
                .map(item -> {
                    try {
                        return embeddingService.getEmbedding(item.getAltText());
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();

        if (likedVectors.isEmpty()) return List.of();

        List<Double> userProfileVector = averageVectors(likedVectors);

        List<Clothing> allItems = swipeStyleRepo.findAll().stream()
                .filter(item -> !likedItemIds.contains(item.getProductId()))
                .filter(item -> !previouslyRecommended.contains(item.getProductId()))
                .toList();

        List<Pair<Clothing, Double>> scored = new ArrayList<>();

        for (Clothing item : allItems) {
            try {
                List<Double> itemVec = embeddingService.getEmbedding(item.getAltText());
                double sim = cosineSimilarity(userProfileVector, itemVec);
                scored.add(Pair.of(item, sim));
            } catch (Exception ignored) {}
        }

        return scored.stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(topN)
                .map(Pair::getKey)
                .toList();
    }
}