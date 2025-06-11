package com.haru.SwipeStyle.Mapper;

import com.haru.SwipeStyle.DTOs.ClothingDTO;
import com.haru.SwipeStyle.Entities.Clothing;
import org.springframework.stereotype.Component;
@Component
public class ClothingMapper {

    public static ClothingDTO toDTO(Clothing clothing) {
        if (clothing == null) {
            return null;
        }

        ClothingDTO dto = new ClothingDTO();
        dto.setProductId(clothing.getProductId());
        dto.setName(clothing.getName());
        dto.setGender(clothing.getGender());
        dto.setPrice(clothing.getPrice());
        dto.setImageUrl(clothing.getImageUrls());
        dto.setProductUrl(clothing.getProductUrl());
        dto.setAltText(clothing.getAltText());

        return dto;
    }

    public static Clothing toEntity(ClothingDTO dto) {
        if (dto == null) {
            return null;
        }

        Clothing clothing = new Clothing();
        clothing.setProductId(dto.getProductId());
        clothing.setName(dto.getName());
        clothing.setGender(dto.getGender());
        clothing.setPrice(dto.getPrice());
        clothing.setImageUrls(dto.getImageUrl());
        clothing.setProductUrl(dto.getProductUrl());
        clothing.setAltText(dto.getAltText());

        return clothing;
    }
}