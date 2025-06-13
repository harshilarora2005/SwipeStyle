package com.haru.SwipeStyle.Services.impl;
import com.haru.SwipeStyle.DTOs.UserDTO;
import com.haru.SwipeStyle.DTOs.UserLoginDTO;
import com.haru.SwipeStyle.DTOs.UserRegistrationDTO;
import com.haru.SwipeStyle.Entities.User;
import com.haru.SwipeStyle.Repository.UserRepo;
import com.haru.SwipeStyle.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//package com.haru.SwipeStyle.Services.impl;
//
//import com.haru.SwipeStyle.DTOs.UserDTO;
//import com.haru.SwipeStyle.DTOs.UserLoginDTO;
//import com.haru.SwipeStyle.DTOs.UserRegistrationDTO;
//import com.haru.SwipeStyle.Repository.UserRepo;
//import com.haru.SwipeStyle.Services.UserServiceImpl;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import java.util.Optional;
//
//@Service
//public class UserService implements UserServiceImpl {
//    @Autowired
//    UserRepo userRepo;
//
//    @Override
//    public UserDTO registerUser(UserRegistrationDTO registrationDTO) {
//        if (userRepo.existsByUsername(registrationDTO.getUsername())) {
//            throw new RuntimeException("Username already exists");
//        }
//        if (userRepo.existsByEmail(registrationDTO.getEmail())) {
//            throw new RuntimeException("Email already exists");
//        }
//
//
//        return null;
//    }
//
//    @Override
//    public Optional<UserDTO> loginUser(UserLoginDTO loginDTO) {
//        return Optional.empty();
//    }
//
//    @Override
//    public Optional<UserDTO> getUserById(Long id) {
//        return Optional.empty();
//    }
//
//    @Override
//    public Optional<UserDTO> getUserByUsername(String username) {
//        return Optional.empty();
//    }
//
//    @Override
//    public UserDTO updateUser(Long id, UserDTO userDTO) {
//        return null;
//    }
//
//    @Override
//    public void deleteUser(Long id) {
//
//    }
//
//    @Override
//    public boolean existsByUsername(String username) {
//        return false;
//    }
//
//    @Override
//    public boolean existsByEmail(String email) {
//        return false;
//    }
//}

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
    public void updateUserGender(String username, String gender) {
        userRepo.updateUserGender(username, gender);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepo.existsByUsername(username);
    }
    @Override
    public boolean userExistsByEmail(String email) {
        return userRepo.findByEmail(email).isPresent();
    }
}
