package com.haru.SwipeStyle.Services;

import com.haru.SwipeStyle.DTOs.ClothingDTO;
import com.haru.SwipeStyle.Entities.Clothing;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Pageable;

public interface ClothingService {

    List<Clothing> getAllClothingItems();

    List<Clothing> getAllClothingItemsList(Pageable pageable);

    Optional<Clothing> getClothingById(Long id);

    List<ClothingDTO> getProductsByGender(String gender);

    List<ClothingDTO> getAllProductsAsDTO();

    Clothing saveClothing(Clothing clothing);

    List<Clothing> saveAllClothing(List<Clothing> clothingList);

    void deleteClothingById(Long id);

    boolean existsById(Long id);

    LocalDateTime getMaxCreatedAt();

    Set<String> findExistingProductIds(Set<String> productIds);

    long getTotalCount();

    long getId(String productId);
}