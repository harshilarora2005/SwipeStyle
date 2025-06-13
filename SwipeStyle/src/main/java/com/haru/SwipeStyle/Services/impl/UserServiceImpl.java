package com.haru.SwipeStyle.Services.impl;
import com.haru.SwipeStyle.DTOs.UserDTO;
import com.haru.SwipeStyle.Entities.User;
import com.haru.SwipeStyle.Mapper.ClothingMapper;
import com.haru.SwipeStyle.Mapper.UserMapper;
import com.haru.SwipeStyle.Repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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
public class UserService {
    @Autowired
    private UserRepo userRepo;

    public User findOrCreateUser(String auth0Id, String email, String username, String profilePictureUrl) {
        return userRepo.findByAuth0Id(auth0Id)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setAuth0Id(auth0Id);
                    newUser.setEmail(email);
                    newUser.setUsername(username != null ? username : email.split("@")[0]);
                    newUser.setProfilePictureUrl(profilePictureUrl);
                    newUser.setIsActive(true);
                    return userRepo.save(newUser);
                });
    }
    public User findUserByAuth0Id(String auth0Id) {
        return userRepo.findByAuth0Id(auth0Id).orElseThrow(() -> new UsernameNotFoundException("user not found"));
    }
    public User updateUser(String auth0Id, UserDTO userDTO) {
        User user = userRepo.findByAuth0Id(auth0Id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (userDTO.getUsername() != null) {
            user.setUsername(userDTO.getUsername());
        }
        if (userDTO.getGender() != null) {
            user.setGender(userDTO.getGender());
        }

        return userRepo.save(user);
    }

    public void deleteUser(String auth0Id) {
        User user = userRepo.findByAuth0Id(auth0Id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepo.delete(user);
    }


}
