package com.haru.SwipeStyle.Services;

import com.haru.SwipeStyle.DTOs.UserLoginDTO;
import com.haru.SwipeStyle.DTOs.UserDTO;
import com.haru.SwipeStyle.DTOs.UserRegistrationDTO;
import com.haru.SwipeStyle.Entities.User;

public interface UserService {
    User registerUser(UserRegistrationDTO userDTO);
    User loginUser(UserLoginDTO loginDTO);

    void updateUserGender(String username, String gender);
    boolean existsByUsername(String username);

    boolean userExistsByEmail(String email);
}
