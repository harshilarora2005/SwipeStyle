package com.haru.SwipeStyle.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ZaraProduct {
    private String productId;
    private String name;
    private String price;
    private String imageUrl;
    private String productUrl;
    private String color;
    private String altText;
    private int pageNumber;

    // Constructors
    public ZaraProduct() {}

    public ZaraProduct(String productId, String name, String price, String imageUrl,
                       String productUrl, String color, String altText, int pageNumber) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.productUrl = productUrl;
        this.color = color;
        this.altText = altText;
        this.pageNumber = pageNumber;
    }


    @Override
    public String toString() {
        return "ZaraProduct{" +
                "productId='" + productId + '\'' +
                ", name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", color='" + color + '\'' +
                ", pageNumber=" + pageNumber +
                '}';
    }
}
