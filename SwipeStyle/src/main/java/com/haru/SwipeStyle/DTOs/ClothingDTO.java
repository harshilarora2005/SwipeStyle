package com.haru.SwipeStyle.DTOs;


import com.haru.SwipeStyle.Entities.Category;
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
    private String category;

    public ClothingDTO() {}
    public ClothingDTO(String productId, String name, String gender, String price, String imageUrl, String productUrl, String altText) {
        this.productId = productId;
        this.name = name;
        this.gender = gender;
        this.price = price;
        this.imageUrl = imageUrl;
        this.productUrl = productUrl;
        this.altText = altText;
    }

}

