package com.contactmanager.service;

import com.contactmanager.dto.AuthResponse;
import com.contactmanager.dto.ChangePasswordRequest;
import com.contactmanager.dto.LoginRequest;
import com.contactmanager.dto.RegisterRequest;
import com.contactmanager.entity.User;
import com.contactmanager.repository.UserRepository;
import com.contactmanager.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        validateRegisterRequest(request);

        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(BAD_REQUEST, "Email is already in use");
        }

        if (request.getPhoneNumber() != null && userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new ResponseStatusException(BAD_REQUEST, "Phone number is already in use");
        }

        User user = User.builder()
                .firstName(request.getFirstName().trim())
                .lastName(request.getLastName().trim())
                .email(normalizeNullable(request.getEmail()))
                .phoneNumber(normalizeNullable(request.getPhoneNumber()))
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);

        return AuthResponse.builder()
                .message("User registered successfully")
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().trim();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);

        return AuthResponse.builder()
                .message("Login successful")
                .token(token)
                .build();
    }

    @Override
    @Transactional
    public AuthResponse changePassword(String authenticatedEmail, ChangePasswordRequest request) {
        User user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new ResponseStatusException(BAD_REQUEST, "Current password is incorrect");
        }

        if (request.getCurrentPassword().equals(request.getNewPassword())) {
            throw new ResponseStatusException(BAD_REQUEST, "New password must be different from current password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtUtil.generateToken(userDetails);

        return AuthResponse.builder()
                .message("Password changed successfully")
                .token(token)
                .build();
    }

    private void validateRegisterRequest(RegisterRequest request) {
        String email = normalizeNullable(request.getEmail());
        String phone = normalizeNullable(request.getPhoneNumber());

        if (email == null && phone == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Either email or phoneNumber is required");
        }

        request.setEmail(email);
        request.setPhoneNumber(phone);
    }

    private String normalizeNullable(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}


