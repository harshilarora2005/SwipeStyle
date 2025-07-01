package com.haru.SwipeStyle.Services;

import com.haru.SwipeStyle.DTOs.ClothingDTO;
import java.util.*;
public interface SwipeStyleService {
    public List <ClothingDTO> scrapeProducts(String url,int scroll,String gender);
    List<ClothingDTO> scrapeProductsWithRange(String url, int startPage, int endPage, String gender);
}
