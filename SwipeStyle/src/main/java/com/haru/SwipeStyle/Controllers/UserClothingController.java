package com.haru.SwipeStyle.Controllers;

import com.haru.SwipeStyle.DTOs.ClothingDTO;
import com.haru.SwipeStyle.DTOs.UserClothingDTO;
import com.haru.SwipeStyle.Entities.InteractionType;
import com.haru.SwipeStyle.Entities.UserClothing;
import com.haru.SwipeStyle.Services.UserClothingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-clothing")
public class UserClothingController {
    @Autowired
    private UserClothingService userClothingService;

    @PostMapping("/save-interaction")
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
    public ResponseEntity<?> getInteractions(@RequestParam long userId, @RequestParam InteractionType interactionType) {
        try{
            List<ClothingDTO> response = userClothingService.findByInteraction(userId,interactionType);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
