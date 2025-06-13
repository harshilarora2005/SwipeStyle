package com.haru.SwipeStyle.Services;

import com.haru.SwipeStyle.DTOs.UserLoginDTO;
import com.haru.SwipeStyle.DTOs.UserDTO;
import com.haru.SwipeStyle.Entities.User;

public interface UserService {
    User registerUser(UserDTO userDTO);
    User loginUser(UserLoginDTO loginDTO);

    boolean existsByUsername(String username);

    boolean userExistsByEmail(String email);
}
