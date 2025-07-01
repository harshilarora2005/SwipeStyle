package com.haru.SwipeStyle.Entities;

import com.haru.SwipeStyle.DTOs.ClothingDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationRequest {
    private List<ClothingDTO> likedItems;
    private Set<String> previouslyRecommended;
}