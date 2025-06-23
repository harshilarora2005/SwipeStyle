package com.haru.SwipeStyle.Controllers;

import com.haru.SwipeStyle.DTOs.UserLoginDTO;
import com.haru.SwipeStyle.DTOs.UserDTO;
import com.haru.SwipeStyle.DTOs.UserRegistrationDTO;
import com.haru.SwipeStyle.Entities.User;
import com.haru.SwipeStyle.Services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"}
,allowCredentials = "true")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegistrationDTO userDTO) {
        User user = userService.registerUser(userDTO);
        return ResponseEntity.ok(user);
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDTO loginDTO, HttpServletRequest request) {
        SecurityContextHolder.clearContext();

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        session = request.getSession(true);
        User user = userService.loginUser(loginDTO);
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(user.getUsername(), loginDTO.getPassword());
        Authentication authentication = authenticationManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
        request.getSession().setAttribute("AUTH_TYPE", "FORM_LOGIN");
        return ResponseEntity.ok(user);
    }
    @GetMapping("/exists/{email}")
    public ResponseEntity<Boolean> checkUserExists(@PathVariable String email) {
        return ResponseEntity.ok(userService.userExistsByEmail(email));
    }
    @GetMapping("/me")
    public ResponseEntity<?> getLoggedInUser(Authentication authentication,HttpServletRequest request) {
        System.out.println("=== DEBUG /me endpoint ===");
        System.out.println("Authentication: " + authentication);
        System.out.println("Authentication class: " + (authentication != null ? authentication.getClass().getSimpleName() : "null"));
        System.out.println("Is authenticated: " + (authentication != null ? authentication.isAuthenticated() : "false"));
        System.out.println("Principal: " + (authentication != null ? authentication.getPrincipal() : "null"));
        System.out.println("Session ID: " + request.getSession().getId());
        System.out.println("Session attributes: " + request.getSession().getAttributeNames());
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        Object principal = authentication.getPrincipal();
        UserDTO userDTO = new UserDTO();
        String authType = authentication.getClass().getSimpleName();
        System.out.println("Authentication type: " + authType);
        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            String email = oauthToken.getPrincipal().getAttribute("email");
            String name = oauthToken.getPrincipal().getAttribute("name");
            String picture = oauthToken.getPrincipal().getAttribute("picture");
            userDTO.setEmail(email);
            userDTO.setUsername(name);
            userDTO.setProfilePictureUrl(picture);
            userDTO.setGender("UNISEX");
            if(!userService.userExistsByEmail(email)) {
                userService.saveUser(userDTO);
            }
            return ResponseEntity.ok(userDTO);
        }

        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            if (principal instanceof UserDetails userDetails) {
                String username = userDetails.getUsername();
                User user = userService.findByUsername(username);
                if (user != null) {
                    return ResponseEntity.ok(user);
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
                }
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unsupported authentication type");
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response,Authentication authentication) {
        SecurityContextHolder.clearContext();

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("JSESSIONID".equals(cookie.getName())) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }
        if (authentication instanceof OAuth2AuthenticationToken) {
            String googleLogoutUrl = "https://accounts.google.com/Logout";
            return ResponseEntity.status(HttpStatus.OK).body(
                    Map.of("message", "Logged out from app", "oauthLogoutUrl", googleLogoutUrl)
            );
        }

        return ResponseEntity.ok("Logged out successfully");
    }
    @PutMapping("/updateGender")
    public ResponseEntity<?> updateGender(@RequestBody UserDTO userDTO) {
        System.out.println(userDTO);
        System.out.println("Received request to update gender for user: " + userDTO.getUsername() + " to: " + userDTO.getGender());
        try {
            int rowsAffected = userService.updateUserGender(userDTO.getUsername(), userDTO.getGender());
            System.out.println("Rows affected: " + rowsAffected);
            return ResponseEntity.ok("Updated successfully");
        } catch (Exception e) {
            System.err.println("Error updating gender: " + e.getMessage());
            return ResponseEntity.status(500).body("Update failed: " + e.getMessage());
        }
    }
    @GetMapping("/get-id")
    public ResponseEntity<?> getUserId(@RequestParam String email) {
        try {
            User user = userService.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
            return ResponseEntity.ok(user.getId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to get user ID for email: " + email);
        }
    }
}
