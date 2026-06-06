package com.contactmanager.contact_management_system.controller;

import com.contactmanager.contact_management_system.dto.request.ChangePasswordRequest;
import com.contactmanager.contact_management_system.dto.request.LoginRequest;
import com.contactmanager.contact_management_system.dto.request.RegisterRequest;
import com.contactmanager.contact_management_system.dto.response.AuthResponse;
import com.contactmanager.contact_management_system.entity.User;
import com.contactmanager.contact_management_system.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication endpoints (register, login, change-password).
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/auth/register — register a new user.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("POST /api/auth/register — email: {}", request.getEmail());
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * POST /api/auth/login — authenticate and receive a JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("POST /api/auth/login — email: {}", request.getEmail());
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * POST /api/auth/change-password — change the authenticated user's password.
     */
    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody ChangePasswordRequest request) {
        log.info("POST /api/auth/change-password — user: {}", currentUser.getEmail());
        authService.changePassword(currentUser, request);
        return ResponseEntity.noContent().build();
    }
}
