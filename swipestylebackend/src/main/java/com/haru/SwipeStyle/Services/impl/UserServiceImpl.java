package com.haru.SwipeStyle.Services.impl;
import com.haru.SwipeStyle.DTOs.UserDTO;
import com.haru.SwipeStyle.DTOs.UserLoginDTO;
import com.haru.SwipeStyle.DTOs.UserRegistrationDTO;
import com.haru.SwipeStyle.Entities.User;
import com.haru.SwipeStyle.Repository.UserRepo;
import com.haru.SwipeStyle.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User saveUser(UserDTO dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setUsername(dto.getUsername());
        user.setGender(dto.getGender());
        user.setRole(com.haru.SwipeStyle.Entities.Role.USER);
        user.setPassword(UUID.randomUUID().toString());
        user.setAuthType("OAUTH");
        return userRepo.save(user);
    }
    @Override
    public User registerUser(UserRegistrationDTO dto) {
        if (userRepo.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setUsername(dto.getUsername());
        user.setGender(dto.getGender());
        user.setRole(com.haru.SwipeStyle.Entities.Role.USER);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        return userRepo.save(user);
    }

    @Override
    public User loginUser(UserLoginDTO loginDTO) {
        String identifier = loginDTO.getUsernameOrEmail();
        return userRepo.findByUsernameOrEmail(identifier, identifier)
                .filter(user -> passwordEncoder.matches(loginDTO.getPassword(), user.getPassword()))
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
    }

    @Override
    public int updateUserGender(String username, String gender) {
        System.out.println("UserDTO: username=" + username+ ", gender=" + gender);
        return userRepo.updateUserGender(username, gender);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepo.existsByUsername(username);
    }
    @Override
    public boolean userExistsByEmail(String email) {
        return userRepo.findByEmail(email).isPresent();
    }

    @Override
    public long getUserId(String email) {
        return userRepo.findIdByEmail(email);
    }

    @Override
    public User findByUsername(String username) {
        return userRepo.findByUsername(username).orElse(null);
    }
    @Override
    public User findByEmail(String email) {
        return userRepo.findByEmail(email).orElse(null);
    }
}
