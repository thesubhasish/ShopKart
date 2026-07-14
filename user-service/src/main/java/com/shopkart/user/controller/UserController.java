package com.shopkart.user.controller;

import com.shopkart.user.dto.UserProfileResponse;
import com.shopkart.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    /**
     * Protected endpoint - requires "Authorization: Bearer <token>" header.
     * @AuthenticationPrincipal injects the User that JwtAuthFilter resolved
     * and placed into the SecurityContext for this request.
     */
    @GetMapping("/me")
    public UserProfileResponse getCurrentUser(@AuthenticationPrincipal User currentUser) {
        return UserProfileResponse.from(currentUser);
    }
}
