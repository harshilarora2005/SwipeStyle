package com.haru.SwipeStyle.DTOs;

import com.haru.SwipeStyle.Entities.Role;
import lombok.Data;

import java.util.EnumSet;

@Data
public class UserDTO {
    private String email;
    private String username;
    private String password;
    private String gender;
    private String profilePictureUrl;
}
