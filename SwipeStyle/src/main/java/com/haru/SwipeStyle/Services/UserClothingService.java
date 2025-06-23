package com.haru.SwipeStyle.Services;

import com.haru.SwipeStyle.DTOs.UserClothingDTO;
import com.haru.SwipeStyle.Entities.UserClothing;

public interface UserClothingService {
    public UserClothing findByClothingId(Long clothingId);
    public UserClothing save(UserClothingDTO clothing);
}
