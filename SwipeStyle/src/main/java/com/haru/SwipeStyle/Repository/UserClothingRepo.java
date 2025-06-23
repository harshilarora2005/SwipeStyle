package com.haru.SwipeStyle.Repository;

import com.haru.SwipeStyle.Entities.InteractionType;
import com.haru.SwipeStyle.Entities.UserClothing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserClothingRepo extends JpaRepository<UserClothing, Long> {
    boolean existsByUserIdAndClothingIdAndInteractionType(Long userId, Long clothingId, InteractionType interactionType);
}
