package com.haru.SwipeStyle.DTOs;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ClothingDTO {
    private String productId;
    private String name;
    private String gender;
    private String price;
    private String imageUrl;
    private String productUrl;
    private String altText;

    public ClothingDTO() {}

}

