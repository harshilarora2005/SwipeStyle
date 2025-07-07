package com.haru.SwipeStyle.Mapper;

import com.haru.SwipeStyle.DTOs.ClothingDTO;
import com.haru.SwipeStyle.Entities.Category;
import com.haru.SwipeStyle.Entities.Clothing;
import org.springframework.stereotype.Component;
@Component
public class ClothingMapper {

    public static ClothingDTO toDTO(Clothing clothing) {
        if (clothing == null) {
            return null;
        }

        return new ClothingDTO(
                clothing.getProductId(),
                clothing.getName(),
                clothing.getGender(),
                clothing.getPrice(),
                clothing.getImageUrls(),
                clothing.getProductUrl(),
                clothing.getAltText(),
                clothing.getCategory().toString()
        );
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
        if(dto.getCategory() != null) {
            clothing.setCategory(Category.valueOf(dto.getCategory()));
        }
        else{
            clothing.setCategory(null);
        }


        return clothing;
    }
}