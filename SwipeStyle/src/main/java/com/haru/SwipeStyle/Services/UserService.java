package com.haru.SwipeStyle.Services;

import com.haru.SwipeStyle.DTOs.UserLoginDTO;
import com.haru.SwipeStyle.DTOs.UserDTO;
import com.haru.SwipeStyle.DTOs.UserRegistrationDTO;
import com.haru.SwipeStyle.Entities.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService {
    User registerUser(UserRegistrationDTO userDTO);
    User loginUser(UserLoginDTO loginDTO);

    void updateUserGender(String username, String gender);
    boolean existsByUsername(String username);
    User findByUsername(String username);
    User findByEmail(String email);
    boolean userExistsByEmail(String email);
}
