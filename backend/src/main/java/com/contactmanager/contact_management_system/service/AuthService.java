package com.contactmanager.contact_management_system.service;

import com.contactmanager.contact_management_system.dto.request.ChangePasswordRequest;
import com.contactmanager.contact_management_system.dto.request.LoginRequest;
import com.contactmanager.contact_management_system.dto.request.RegisterRequest;
import com.contactmanager.contact_management_system.dto.response.AuthResponse;
import com.contactmanager.contact_management_system.entity.User;
import com.contactmanager.contact_management_system.entity.enums.Role;
import com.contactmanager.contact_management_system.exception.BadRequestException;
import com.contactmanager.contact_management_system.exception.DuplicateResourceException;
import com.contactmanager.contact_management_system.repository.UserRepository;
import com.contactmanager.contact_management_system.security.JwtTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service handling user registration, login, and password changes.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    /**
     * Registers a new user and returns a JWT token.
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed — email already in use: {}", request.getEmail());
            throw new DuplicateResourceException("Email is already registered: " + request.getEmail());
        }

        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()
                && userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            log.warn("Registration failed — phone number already in use: {}", request.getPhoneNumber());
            throw new DuplicateResourceException("Phone number is already registered: " + request.getPhoneNumber());
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with id: {}", savedUser.getId());

        String token = jwtTokenProvider.generateToken(savedUser);
        return buildAuthResponse(token, savedUser);
    }

    /**
     * Authenticates a user and returns a JWT token.
     */
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = (User) authentication.getPrincipal();
        String token = jwtTokenProvider.generateToken(user);

        log.info("User '{}' logged in successfully", user.getEmail());
        return buildAuthResponse(token, user);
    }

    /**
     * Changes the authenticated user's password after verifying the current password.
     */
    @Transactional
    public void changePassword(User currentUser, ChangePasswordRequest request) {
        log.info("Password change request for user: {}", currentUser.getEmail());

        if (!passwordEncoder.matches(request.getCurrentPassword(), currentUser.getPasswordHash())) {
            log.warn("Password change failed — incorrect current password for user: {}", currentUser.getEmail());
            throw new BadRequestException("Current password is incorrect");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("New password and confirmation do not match");
        }

        currentUser.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(currentUser);

        log.info("Password changed successfully for user: {}", currentUser.getEmail());
    }

    // ─── Helper ───────────────────────────────────────────────────────────────

    private AuthResponse buildAuthResponse(String token, User user) {
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .build();
    }
}
