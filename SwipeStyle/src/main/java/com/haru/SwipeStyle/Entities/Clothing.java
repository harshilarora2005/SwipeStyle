package com.haru.SwipeStyle.Entities;


import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "clothing_items", indexes = {
        @Index(name = "idx_price", columnList = "price"),
        @Index(name = "idx_created_at", columnList = "createdAt")
})
public class Clothing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @NotBlank
    private String productId;

    @Column(nullable = false)
    @NotBlank
    private String name;

    @Column
    private String gender;

    @Column
    private String price;

    @Column(name = "image_url")
    private String imageUrls;

    @Column(name="product_url")
    private String productUrl;

    @Column(name = "alt_text",columnDefinition = "TEXT")
    private String altText;

    @Column(name="created_at",nullable = false)
    private LocalDateTime createdAt;

    @Column(name="updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "clothing", cascade = CascadeType.ALL, fetch = FetchType.LAZY,orphanRemoval = true)
    private List<UserClothing> userInteractions = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
