package com.haru.SwipeStyle.Controllers;

import com.haru.SwipeStyle.DTOs.ClothingDTO;
import com.haru.SwipeStyle.Entities.Clothing;
import com.haru.SwipeStyle.Mapper.ClothingMapper;
import com.haru.SwipeStyle.Repository.SwipeStyleRepo;
import com.haru.SwipeStyle.Services.UrlProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/swipe-style/")
public class SwipeStyleController {

    @Autowired
    private SwipeStyleRepo swipeStyleRepo;


    @Autowired
    private UrlProvider urlProvider;

    @GetMapping("products")
    public ResponseEntity<List<ClothingDTO>> getProducts(
            @RequestParam(defaultValue = "0") int page) {

        Pageable pageable = PageRequest.of(page, 20);
        List<Clothing> clothingList = swipeStyleRepo.findAll(pageable).getContent();
        List<ClothingDTO> clothingDTOs = clothingList.stream().map(ClothingMapper::toDTO).collect(Collectors.toList());

        return ResponseEntity.ok(clothingDTOs);
    }

}