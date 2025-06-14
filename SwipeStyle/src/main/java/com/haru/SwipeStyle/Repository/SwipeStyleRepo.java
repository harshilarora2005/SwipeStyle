package com.haru.SwipeStyle.Repository;

import com.haru.SwipeStyle.DTOs.ClothingDTO;
import com.haru.SwipeStyle.Entities.Clothing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Set;

public interface SwipeStyleRepo extends JpaRepository<Clothing,Long>, JpaSpecificationExecutor<Clothing> {
    @Query("Select MAX(c.createdAt) from Clothing c")
    LocalDateTime getMaxCreatedAt();

    @Query("SELECT c.productId FROM Clothing c WHERE c.productId IN :productIds")
    Set<String> findExistingProductIds(@Param("productIds") Set<String> productIds);

    @Query("SELECT new com.haru.SwipeStyle.DTOs.ClothingDTO(c.productId, c.name, c.price, c.gender, c.imageUrls, c.productUrl, c.altText) FROM Clothing c WHERE c.gender = :gender")
    Set<ClothingDTO> getProductIdsByGender(@Param("gender") String gender);
}
