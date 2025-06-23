package com.haru.SwipeStyle.Services;

import com.haru.SwipeStyle.DTOs.UserClothingDTO;
import com.haru.SwipeStyle.Entities.Clothing;
import com.haru.SwipeStyle.Entities.InteractionType;
import com.haru.SwipeStyle.Entities.UserClothing;

import java.util.List;

public interface UserClothingService {
    public UserClothing findByClothingId(Long clothingId);
    public UserClothing save(UserClothingDTO clothing);
    public List<Clothing> findByInteraction(Long userId, InteractionType interaction);
    public boolean existsByClothingId(Long clothingId);
}
