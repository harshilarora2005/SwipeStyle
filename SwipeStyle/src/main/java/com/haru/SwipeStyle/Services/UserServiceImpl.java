package com.haru.SwipeStyle.Services;

import com.haru.SwipeStyle.DTOs.UserDTO;
import com.haru.SwipeStyle.DTOs.UserLoginDTO;
import org.springframework.stereotype.Service;
import com.haru.SwipeStyle.DTOs.UserRegistrationDTO;
import java.util.Optional;

@Service
public interface UserServiceImpl {
        UserDTO registerUser(UserRegistrationDTO registrationDTO);
        Optional<UserDTO> loginUser(UserLoginDTO loginDTO);
        Optional<UserDTO> getUserById(Long id);
        Optional<UserDTO> getUserByUsername(String username);
        UserDTO updateUser(Long id, UserDTO userDTO);
        void deleteUser(Long id);
        boolean existsByUsername(String username);
        boolean existsByEmail(String email);
}
