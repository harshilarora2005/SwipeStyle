package com.haru.SwipeStyle.Repository;

import com.haru.SwipeStyle.Entities.Clothing;
import com.haru.SwipeStyle.Entities.InteractionType;
import com.haru.SwipeStyle.Entities.UserClothing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserClothingRepo extends JpaRepository<UserClothing, Long> {
    boolean existsByUserIdAndClothingIdAndInteractionType(Long userId, Long clothingId, InteractionType interactionType);

    @Query("SELECT uc.clothing FROM UserClothing uc WHERE uc.user.id = :userId AND uc.interactionType = :type")
    List<Clothing> findClothingByUserAndInteractionType(@Param("userId") Long userId, @Param("type") InteractionType type);

    boolean existsByClothingId(Long clothingId);
}
