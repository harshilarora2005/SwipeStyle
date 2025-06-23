package com.haru.SwipeStyle.Services.impl;

import com.haru.SwipeStyle.DTOs.ClothingDTO;
import com.haru.SwipeStyle.Entities.Clothing;
import com.haru.SwipeStyle.Mapper.ClothingMapper;
import com.haru.SwipeStyle.Repository.SwipeStyleRepo;
import com.haru.SwipeStyle.Services.ClothingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class ClothingServiceImpl implements ClothingService {

    private final SwipeStyleRepo swipeStyleRepo;

    @Autowired
    public ClothingServiceImpl(SwipeStyleRepo swipeStyleRepo) {
        this.swipeStyleRepo = swipeStyleRepo;
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
        return swipeStyleRepo.save(clothing);
    }

    @Override
    public List<Clothing> saveAllClothing(List<Clothing> clothingList) {
        if (clothingList == null || clothingList.isEmpty()) {
            throw new IllegalArgumentException("Clothing list cannot be null or empty");
        }
        return swipeStyleRepo.saveAll(clothingList);
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
}