package com.haru.SwipeStyle.DTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;


@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class UserDTO {
    private String email;
    private String username;
    private String password;
    private String gender;
    private String profilePictureUrl;
    private String authType;
}
