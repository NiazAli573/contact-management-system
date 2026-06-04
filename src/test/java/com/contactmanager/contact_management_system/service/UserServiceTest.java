package com.contactmanager.contact_management_system.service;

import com.contactmanager.contact_management_system.dto.response.UserResponse;
import com.contactmanager.contact_management_system.entity.User;
import com.contactmanager.contact_management_system.entity.enums.Role;
import com.contactmanager.contact_management_system.repository.ContactRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link UserService}.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private ContactRepository contactRepository;

    @InjectMocks private UserService userService;

    private User testUser;

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
                .createdAt(LocalDateTime.of(2025, 1, 15, 10, 0))
                .build();
    }

    @Test
    @DisplayName("GetUserProfile - Returns full profile with correct contact count")
    void getUserProfile_ReturnsCompleteProfile() {
        when(contactRepository.countByOwner(testUser)).thenReturn(5L);

        UserResponse response = userService.getUserProfile(testUser);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getFirstName()).isEqualTo("John");
        assertThat(response.getLastName()).isEqualTo("Doe");
        assertThat(response.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(response.getPhoneNumber()).isEqualTo("1234567890");
        assertThat(response.getRole()).isEqualTo("USER");
        assertThat(response.getTotalContacts()).isEqualTo(5L);
        assertThat(response.getCreatedAt()).isEqualTo(LocalDateTime.of(2025, 1, 15, 10, 0));
    }

    @Test
    @DisplayName("GetUserProfile - Returns zero contact count when user has no contacts")
    void getUserProfile_ZeroContacts() {
        when(contactRepository.countByOwner(testUser)).thenReturn(0L);

        UserResponse response = userService.getUserProfile(testUser);

        assertThat(response.getTotalContacts()).isEqualTo(0L);
    }

    @Test
    @DisplayName("GetUserProfile - Handles user with null phone number")
    void getUserProfile_NullPhoneNumber() {
        User userWithNoPhone = User.builder()
                .id(2L)
                .firstName("Jane")
                .lastName("Roe")
                .email("jane@example.com")
                .phoneNumber(null)
                .passwordHash("hash")
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build();

        when(contactRepository.countByOwner(userWithNoPhone)).thenReturn(0L);

        UserResponse response = userService.getUserProfile(userWithNoPhone);

        assertThat(response.getPhoneNumber()).isNull();
    }
}
