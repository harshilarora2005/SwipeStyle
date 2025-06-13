package com.haru.SwipeStyle.Controllers;

import com.haru.SwipeStyle.DTOs.UserDTO;
import com.haru.SwipeStyle.DTOs.UserRegistrationDTO;
import com.haru.SwipeStyle.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class OAuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/success")
    public ResponseEntity<?> loginSuccess(@AuthenticationPrincipal OAuth2User oAuthUser) {
        String email = oAuthUser.getAttribute("email");
        String name = oAuthUser.getAttribute("name");
        String picture = oAuthUser.getAttribute("picture");
        if (!userService.userExistsByEmail(email)) {
            UserRegistrationDTO newUser = new UserRegistrationDTO();
            newUser.setEmail(email);
            newUser.setUsername(name);
            newUser.setProfilePictureUrl(picture);
            newUser.setGender("unisex");

            userService.registerUser(newUser);
        }
        return ResponseEntity.ok("OAuth login successful: " + oAuthUser.getAttributes());
    }
}
