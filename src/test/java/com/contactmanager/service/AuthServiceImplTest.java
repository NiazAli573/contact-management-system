package com.contactmanager.service;

import com.contactmanager.dto.AuthResponse;
import com.contactmanager.dto.ChangePasswordRequest;
import com.contactmanager.dto.LoginRequest;
import com.contactmanager.dto.RegisterRequest;
import com.contactmanager.entity.User;
import com.contactmanager.repository.UserRepository;
import com.contactmanager.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthServiceImpl Tests")
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private ChangePasswordRequest changePasswordRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .password("Password123")
                .build();

        loginRequest = LoginRequest.builder()
                .email("john@example.com")
                .password("Password123")
                .build();

        changePasswordRequest = ChangePasswordRequest.builder()
                .currentPassword("Password123")
                .newPassword("NewPassword456")
                .build();

        testUser = User.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .password("hashedPassword")
                .build();
    }

    @Test
    @DisplayName("Register: success")
    void testRegister_Success() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(registerRequest.getPhoneNumber())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("User registered successfully", response.getMessage());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Register: duplicate email throws exception")
    void testRegister_DuplicateEmail() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Register: neither email nor phone throws exception")
    void testRegister_NoEmailOrPhone() {
        registerRequest.setEmail(null);
        registerRequest.setPhoneNumber(null);

        assertThrows(ResponseStatusException.class, () -> authService.register(registerRequest));
    }

    @Test
    @DisplayName("Login: success returns JWT")
    void testLogin_Success() {
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        String token = "jwt-token-123";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn(token);

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("Login successful", response.getMessage());
        assertEquals(token, response.getToken());
    }

    @Test
    @DisplayName("Login: wrong password throws exception")
    void testLogin_WrongPassword() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED));

        assertThrows(ResponseStatusException.class, () -> authService.login(loginRequest));
    }

    @Test
    @DisplayName("ChangePassword: success")
    void testChangePassword_Success() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), testUser.getPassword()))
                .thenReturn(true);
        when(passwordEncoder.encode(changePasswordRequest.getNewPassword())).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername(testUser.getEmail())).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("new-jwt-token");

        AuthResponse response = authService.changePassword(testUser.getEmail(), changePasswordRequest);

        assertNotNull(response);
        assertEquals("Password changed successfully", response.getMessage());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("ChangePassword: wrong current password throws exception")
    void testChangePassword_WrongCurrentPassword() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), testUser.getPassword()))
                .thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> authService.changePassword(testUser.getEmail(), changePasswordRequest));
    }

    @Test
    @DisplayName("ChangePassword: user not found throws exception")
    void testChangePassword_UserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> authService.changePassword("nonexistent@example.com", changePasswordRequest));
    }

    @Test
    @DisplayName("ChangePassword: same password as current throws exception")
    void testChangePassword_SamePassword() {
        ChangePasswordRequest samePasswordRequest = ChangePasswordRequest.builder()
                .currentPassword("Password123")
                .newPassword("Password123")
                .build();

        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(samePasswordRequest.getCurrentPassword(), testUser.getPassword()))
                .thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> authService.changePassword(testUser.getEmail(), samePasswordRequest));
    }
}

