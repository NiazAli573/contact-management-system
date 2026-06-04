package com.contactmanager.contact_management_system.controller;

import com.contactmanager.contact_management_system.dto.response.UserResponse;
import com.contactmanager.contact_management_system.entity.User;
import com.contactmanager.contact_management_system.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for user profile operations.
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * GET /api/users/me — returns the authenticated user's profile.
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile(@AuthenticationPrincipal User currentUser) {
        log.info("GET /api/users/me — user: {}", currentUser.getEmail());
        return ResponseEntity.ok(userService.getUserProfile(currentUser));
    }
}
