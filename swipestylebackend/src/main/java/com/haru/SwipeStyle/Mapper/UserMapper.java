package com.haru.SwipeStyle.Mapper;

import com.haru.SwipeStyle.DTOs.UserDTO;
import com.haru.SwipeStyle.Entities.Role;
import com.haru.SwipeStyle.Entities.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserDTO dto) {
        if (dto == null) return null;

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setGender(dto.getGender());
        user.setPassword(dto.getPassword());
        user.setProfilePictureUrl(dto.getProfilePictureUrl());
        return user;
    }

    public UserDTO toDTO(User user) {
        if (user == null) return null;

        UserDTO dto = new UserDTO();
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setGender(user.getGender());
        dto.setProfilePictureUrl(user.getProfilePictureUrl());
        return dto;
    }
}
