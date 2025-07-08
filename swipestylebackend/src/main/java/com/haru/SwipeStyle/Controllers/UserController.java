package com.haru.SwipeStyle.Controllers;

import com.haru.SwipeStyle.DTOs.UserLoginDTO;
import com.haru.SwipeStyle.DTOs.UserDTO;
import com.haru.SwipeStyle.DTOs.UserRegistrationDTO;
import com.haru.SwipeStyle.Entities.User;
import com.haru.SwipeStyle.Services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"}, allowCredentials = "true")
@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management API", description = "Endpoints for user registration, authentication, and profile management")
public class UserController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with email, username, and password."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid registration data")
    })
    public ResponseEntity<?> register(@RequestBody UserRegistrationDTO userDTO) {
        User user = userService.registerUser(userDTO);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    @Operation(
            summary = "Login user",
            description = "Authenticates the user using email and password, and creates a session."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<?> login(
            @RequestBody UserLoginDTO loginDTO,
            HttpServletRequest request,
            HttpServletResponse response) {

        try {
            Authentication authRequest = new UsernamePasswordAuthenticationToken(
                    loginDTO.getUsernameOrEmail(),
                    loginDTO.getPassword()
            );

            Authentication authResult = authenticationManager.authenticate(authRequest);
            SecurityContextHolder.getContext().setAuthentication(authResult);
            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
            System.out.println("Authentication successful: " + authResult.isAuthenticated());
            System.out.println("Session ID: " + session.getId());
            System.out.println("Principal: " + authResult.getPrincipal());
            System.out.println("Authentication: " + authResult.isAuthenticated());
            System.out.println("SecurityContext saved to session");
            User user = userService.loginUser(loginDTO);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @GetMapping("/exists/{email}")
    @Operation(
            summary = "Check if user exists",
            description = "Checks whether a user account exists by email."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email existence returned")
    })
    public ResponseEntity<Boolean> checkUserExists(
            @Parameter(description = "Email to check") @PathVariable String email) {
        return ResponseEntity.ok(userService.userExistsByEmail(email));
    }

    @GetMapping("/me")
    @Operation(
            summary = "Get current authenticated user",
            description = "Returns details of the logged-in user, supporting both OAuth2 and form login."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User info returned"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<?> getLoggedInUser(
            Authentication authentication,
            HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        System.out.println("Session exists: " + (session != null));
        System.out.println("Session ID: " + (session != null ? session.getId() : "null"));
        System.out.println("Authentication from parameter: " + authentication);
        System.out.println("Authentication from SecurityContext: " + SecurityContextHolder.getContext().getAuthentication());

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            String email = oauthToken.getPrincipal().getAttribute("email");
            String name = oauthToken.getPrincipal().getAttribute("name");
            String picture = oauthToken.getPrincipal().getAttribute("picture");
            UserDTO userDTO = new UserDTO();
            userDTO.setEmail(email);
            userDTO.setUsername(name);
            userDTO.setProfilePictureUrl(picture);
            userDTO.setGender("UNISEX");

            if (!userService.userExistsByEmail(email)) {
                userService.saveUser(userDTO);
            }

            return ResponseEntity.ok(userDTO);
        }

        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            if (principal instanceof UserDetails userDetails) {
                String username = userDetails.getUsername();
                User user = userService.findByEmail(username);
                if (user != null) {
                    return ResponseEntity.ok(user);
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
                }
            }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unsupported authentication type");
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Debug logging
        System.out.println("Logout - Authentication: " + authentication);
        System.out.println("Logout - Is authenticated: " + (authentication != null ? authentication.isAuthenticated() : "null"));

        // 1. Invalidate session first
        HttpSession session = request.getSession(false);
        if (session != null) {
            System.out.println("Invalidating session: " + session.getId());
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        Cookie[] cookies = request.getCookies();
        System.out.println("Cookies array: " + (cookies == null ? "null" : "length=" + cookies.length));
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("SESSION".equals(cookie.getName()) || "JSESSIONID".equals(cookie.getName())) {
                    Cookie expiredCookie = new Cookie(cookie.getName(), null);
                    expiredCookie.setPath("/");
                    expiredCookie.setMaxAge(0);
                    expiredCookie.setHttpOnly(true);
                    response.addCookie(expiredCookie);
                    System.out.println("Cleared cookie: " + cookie.getName());
                }
            }
        }

        if (authentication instanceof OAuth2AuthenticationToken) {
            String googleLogoutUrl = "https://accounts.google.com/Logout";
            return ResponseEntity.ok(Map.of(
                    "message", "Logged out from app",
                    "oauthLogoutUrl", googleLogoutUrl
            ));
        }

        return ResponseEntity.ok("Logged out successfully");
    }


    @PutMapping("/updateGender")
    @Operation(
            summary = "Update user gender",
            description = "Updates the gender of the user profile."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Gender updated successfully"),
            @ApiResponse(responseCode = "500", description = "Update failed")
    })
    public ResponseEntity<?> updateGender(@RequestBody UserDTO userDTO) {
        try {
            int rowsAffected = userService.updateUserGender(userDTO.getUsername(), userDTO.getGender());
            return ResponseEntity.ok("Updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Update failed: " + e.getMessage());
        }
    }

    @GetMapping("/get-id")
    @Operation(
            summary = "Get user ID by email",
            description = "Returns the internal user ID for a given email."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User ID returned"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Error retrieving user ID")
    })
    public ResponseEntity<?> getUserId(
            @Parameter(description = "Email address of the user") @RequestParam String email) {
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
