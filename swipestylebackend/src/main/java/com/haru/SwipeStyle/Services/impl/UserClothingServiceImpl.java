package com.haru.SwipeStyle.Services.impl;

import com.haru.SwipeStyle.DTOs.ClothingDTO;
import com.haru.SwipeStyle.DTOs.UserClothingDTO;
import com.haru.SwipeStyle.Entities.Clothing;
import com.haru.SwipeStyle.Entities.InteractionType;
import com.haru.SwipeStyle.Entities.User;
import com.haru.SwipeStyle.Entities.UserClothing;
import com.haru.SwipeStyle.Exceptions.DuplicateResourceException;
import com.haru.SwipeStyle.Mapper.ClothingMapper;
import com.haru.SwipeStyle.Repository.SwipeStyleRepo;
import com.haru.SwipeStyle.Repository.UserClothingRepo;
import com.haru.SwipeStyle.Repository.UserRepo;
import com.haru.SwipeStyle.Services.UserClothingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserClothingServiceImpl implements UserClothingService {
    @Autowired
    UserClothingRepo repo;
    @Autowired
    UserRepo userRepo;
    @Autowired
    SwipeStyleRepo clothingRepo;

    @Override
    public UserClothing findByClothingId(Long clothingId) {
        return null;
    }

    @Override
    public UserClothing save(UserClothingDTO clothing) {
        User user = userRepo.findById(clothing.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Clothing clothing1 = clothingRepo.findById(clothing.getClothingId())
                .orElseThrow(() -> new RuntimeException("Clothing not found"));
        if (repo.existsByUserIdAndClothingIdAndInteractionType(
                clothing.getUserId(), clothing.getClothingId(), clothing.getInteractionType())) {
            throw new DuplicateResourceException(
                    String.format("User %d already has %s interaction with clothing %d",
                            clothing.getUserId(), clothing.getInteractionType(), clothing.getClothingId()));
        }
        UserClothing userClothing = new UserClothing();
        userClothing.setUser(user);
        userClothing.setClothing(clothing1);
        userClothing.setInteractionType(clothing.getInteractionType());
        UserClothing savedInteraction = repo.save(userClothing);
        System.out.println("Successfully created user-clothing interaction with ID: "+savedInteraction.getId());

        return savedInteraction;
    }

    @Override
    public List<ClothingDTO> findByInteraction(Long userId, InteractionType interactionType) {
        List<Clothing> clothing =  repo.findClothingByUserAndInteractionType(userId,interactionType);
        return clothing.stream().map(ClothingMapper::toDTO).collect(Collectors.toList());
    }
    @Override
    public boolean existsByClothingId(Long clothingId) {
        return repo.existsByClothingId(clothingId);
    }
}
