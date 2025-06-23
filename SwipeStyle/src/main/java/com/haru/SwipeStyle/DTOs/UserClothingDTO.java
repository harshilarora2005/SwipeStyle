package com.haru.SwipeStyle.DTOs;

import com.haru.SwipeStyle.Entities.InteractionType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserClothingDTO {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Clothing ID is required")
    private Long clothingId;

    @NotNull(message = "Interaction type is required")
    private InteractionType interactionType;

}
