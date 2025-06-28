package com.haru.SwipeStyle.Controllers;

import com.haru.SwipeStyle.DTOs.ClothingDTO;
import com.haru.SwipeStyle.DTOs.UserClothingDTO;
import com.haru.SwipeStyle.Entities.InteractionType;
import com.haru.SwipeStyle.Entities.UserClothing;
import com.haru.SwipeStyle.Services.UserClothingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-clothing")
@Tag(name = "User Clothing API", description = "Endpoints for saving and retrieving user interactions with clothing")
public class UserClothingController {

    @Autowired
    private UserClothingService userClothingService;

    @PostMapping("/save-interaction")
    @Operation(
            summary = "Save user interaction with clothing",
            description = "Stores an interaction (e.g., LIKE, SKIP) between a user and a clothing item. Returns HTTP 409 if interaction already exists."
    )
    public ResponseEntity<?> saveInteraction(@Valid @RequestBody UserClothingDTO dto) {
        boolean exists = userClothingService.existsByClothingId(dto.getClothingId());
        if (!exists) {
            try {
                UserClothing interaction = userClothingService.save(dto);
                return new ResponseEntity<>(interaction, HttpStatus.CREATED);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    @GetMapping("/get-interactions")
    @Operation(
            summary = "Get user interactions by type",
            description = "Retrieves a list of clothing items the user has interacted with, filtered by interaction type."
    )
    public ResponseEntity<?> getInteractions(
            @RequestParam long userId,
            @RequestParam InteractionType interactionType) {
        try {
            List<ClothingDTO> response = userClothingService.findByInteraction(userId, interactionType);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
