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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link AuthService}.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtTokenService jwtTokenProvider;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks private AuthService authService;

    private User testUser;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("1234567890")
                .passwordHash("hashedPassword")
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build();

        registerRequest = new RegisterRequest();
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setEmail("john.doe@example.com");
        registerRequest.setPhoneNumber("1234567890");
        registerRequest.setPassword("password123");
    }

    // ─── Register Tests ───────────────────────────────────────────────────────

    @Test
    @DisplayName("Register - Success: new user is saved and token returned")
    void register_Success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtTokenProvider.generateToken(any())).thenReturn("jwt-token");

        AuthResponse response = authService.register(registerRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getEmail()).isEqualTo("john.doe@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Register - Failure: duplicate email throws DuplicateResourceException")
    void register_DuplicateEmail_ThrowsException() {
        when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Email is already registered");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Register - Failure: duplicate phone throws DuplicateResourceException")
    void register_DuplicatePhone_ThrowsException() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByPhoneNumber("1234567890")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Phone number is already registered");
    }

    // ─── Login Tests ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Login - Success: valid credentials return token")
    void login_Success() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("john.doe@example.com");
        loginRequest.setPassword("password123");

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(testUser, null, testUser.getAuthorities());
        when(authenticationManager.authenticate(any())).thenReturn(authToken);
        when(jwtTokenProvider.generateToken(any())).thenReturn("jwt-token");

        AuthResponse response = authService.login(loginRequest);

        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    @DisplayName("Login - Failure: bad credentials throws exception")
    void login_BadCredentials_ThrowsException() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("john.doe@example.com");
        loginRequest.setPassword("wrong");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class);
    }

    // ─── Change Password Tests ────────────────────────────────────────────────

    @Test
    @DisplayName("ChangePassword - Success: password updated when current matches")
    void changePassword_Success() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("currentPass");
        request.setNewPassword("newPass123");
        request.setConfirmPassword("newPass123");

        when(passwordEncoder.matches("currentPass", testUser.getPasswordHash())).thenReturn(true);
        when(passwordEncoder.encode("newPass123")).thenReturn("newHashedPass");
        when(userRepository.save(any())).thenReturn(testUser);

        assertThatCode(() -> authService.changePassword(testUser, request))
                .doesNotThrowAnyException();

        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("ChangePassword - Failure: wrong current password throws BadRequestException")
    void changePassword_WrongCurrentPassword_ThrowsException() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("wrongPass");
        request.setNewPassword("newPass123");
        request.setConfirmPassword("newPass123");

        when(passwordEncoder.matches("wrongPass", testUser.getPasswordHash())).thenReturn(false);

        assertThatThrownBy(() -> authService.changePassword(testUser, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Current password is incorrect");
    }

    @Test
    @DisplayName("ChangePassword - Failure: mismatched new passwords throws BadRequestException")
    void changePassword_MismatchedPasswords_ThrowsException() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("currentPass");
        request.setNewPassword("newPass123");
        request.setConfirmPassword("differentPass");

        when(passwordEncoder.matches("currentPass", testUser.getPasswordHash())).thenReturn(true);

        assertThatThrownBy(() -> authService.changePassword(testUser, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("do not match");
    }
}
