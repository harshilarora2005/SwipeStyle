package com.haru.SwipeStyle.Services.impl;

import com.haru.SwipeStyle.DTOs.UserDTO;
import com.haru.SwipeStyle.DTOs.UserLoginDTO;
import com.haru.SwipeStyle.DTOs.UserRegistrationDTO;
import com.haru.SwipeStyle.Repository.UserRepo;
import com.haru.SwipeStyle.Services.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserServiceImpl {
    @Autowired
    UserRepo userRepo;

    @Override
    public UserDTO registerUser(UserRegistrationDTO registrationDTO) {
        if (userRepo.existsByUsername(registrationDTO.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepo.existsByEmail(registrationDTO.getEmail())) {
            throw new RuntimeException("Email already exists");
        }


        return null;
    }

    @Override
    public Optional<UserDTO> loginUser(UserLoginDTO loginDTO) {
        return Optional.empty();
    }

    @Override
    public Optional<UserDTO> getUserById(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<UserDTO> getUserByUsername(String username) {
        return Optional.empty();
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        return null;
    }

    @Override
    public void deleteUser(Long id) {

    }

    @Override
    public boolean existsByUsername(String username) {
        return false;
    }

    @Override
    public boolean existsByEmail(String email) {
        return false;
    }
}
