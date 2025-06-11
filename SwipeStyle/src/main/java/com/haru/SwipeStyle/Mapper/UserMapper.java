package com.haru.SwipeStyle.Mapper;

import com.haru.SwipeStyle.DTOs.UserDTO;
import com.haru.SwipeStyle.Entities.User;

public class UserMapper {

    private static UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setGender(user.getGender());
        dto.setRole(user.getRole().toString());
        return dto;
    }
}
