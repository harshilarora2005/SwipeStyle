package com.haru.SwipeStyle.DTOs;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
public class ClothingDTO {
    private String productId;
    private String name;
    private String price;
    private String imageUrl;
    private String gender;
    private String productUrl;
    private String altText;


    public ClothingDTO() {}

    public ClothingDTO(String productId, String name, String Gender,String price, String imageUrls, String productUrl, String altText) {
        this.productId = productId;
        this.gender = Gender;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrls;
        this.productUrl = productUrl;
        this.altText = altText;
    }

}

