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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

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
            HttpServletRequest request) {

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
    @Operation(
            summary = "Logout current user",
            description = "Clears session and authentication cookies. Returns a Google logout URL if OAuth2 login was used."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Logout successful")
    })
    public ResponseEntity<?> logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) {

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
