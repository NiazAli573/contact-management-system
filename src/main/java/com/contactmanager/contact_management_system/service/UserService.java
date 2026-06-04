package com.contactmanager.contact_management_system.service;

import com.contactmanager.contact_management_system.dto.response.UserResponse;
import com.contactmanager.contact_management_system.entity.User;
import com.contactmanager.contact_management_system.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for retrieving user profile information.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final ContactRepository contactRepository;

    /**
     * Returns a full profile response for the currently authenticated user.
     */
    @Transactional(readOnly = true)
    public UserResponse getUserProfile(User user) {
        log.debug("Fetching profile for user: {}", user.getEmail());

        long contactCount = contactRepository.countByOwner(user);

        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .totalContacts(contactCount)
                .build();
    }
}
