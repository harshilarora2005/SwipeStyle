package com.haru.SwipeStyle.Controllers;

import com.haru.SwipeStyle.DTOs.UserLoginDTO;
import com.haru.SwipeStyle.DTOs.UserDTO;
import com.haru.SwipeStyle.DTOs.UserRegistrationDTO;
import com.haru.SwipeStyle.Entities.User;
import com.haru.SwipeStyle.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegistrationDTO userDTO) {
        User user = userService.registerUser(userDTO);
        return ResponseEntity.ok(user);
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDTO loginDTO) {
        User user = userService.loginUser(loginDTO);
        return ResponseEntity.ok(user);
    }
    @GetMapping("/exists/{email}")
    public ResponseEntity<Boolean> checkUserExists(@PathVariable String email) {
        return ResponseEntity.ok(userService.userExistsByEmail(email));
    }
    @GetMapping("/me")
    public ResponseEntity<?> getLoggedInUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        return ResponseEntity.ok(authentication.getPrincipal());
    }
}
