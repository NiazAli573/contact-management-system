package com.contactmanager.contact_management_system.repository;

import com.contactmanager.contact_management_system.entity.Contact;
import com.contactmanager.contact_management_system.entity.User;
import com.contactmanager.contact_management_system.entity.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DataJPA tests for {@link ContactRepository} using in-memory H2 database.
 */
@DataJpaTest
@ActiveProfiles("test")
@org.springframework.test.context.ContextConfiguration(classes = com.contactmanager.contact_management_system.ContactManagementSystemApplication.class)
class ContactRepositoryTest {

    @Autowired private ContactRepository contactRepository;
    @Autowired private UserRepository userRepository;

    private User owner;
    private User anotherUser;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .passwordHash("hash")
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .contacts(new ArrayList<>())
                .build();
        owner = userRepository.save(owner);

        anotherUser = User.builder()
                .firstName("Jane")
                .lastName("Roe")
                .email("jane@example.com")
                .passwordHash("hash")
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .contacts(new ArrayList<>())
                .build();
        anotherUser = userRepository.save(anotherUser);

        // Create contacts for owner
        Contact c1 = Contact.builder()
                .firstName("Alice")
                .lastName("Smith")
                .title("Engineer")
                .owner(owner)
                .emails(new ArrayList<>())
                .phones(new ArrayList<>())
                .build();
        Contact c2 = Contact.builder()
                .firstName("Bob")
                .lastName("Johnson")
                .title("Manager")
                .owner(owner)
                .emails(new ArrayList<>())
                .phones(new ArrayList<>())
                .build();
        Contact c3 = Contact.builder()
                .firstName("Carol")
                .lastName("Williams")
                .title("Director")
                .owner(anotherUser)
                .emails(new ArrayList<>())
                .phones(new ArrayList<>())
                .build();

        contactRepository.saveAll(java.util.List.of(c1, c2, c3));
    }

    @Test
    @DisplayName("findByOwner - Returns only contacts belonging to the given user")
    void findByOwner_ReturnsOnlyOwnerContacts() {
        Page<Contact> result = contactRepository.findByOwner(owner, PageRequest.of(0, 10));
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).extracting(Contact::getFirstName)
                .containsExactlyInAnyOrder("Alice", "Bob");
    }

    @Test
    @DisplayName("searchByOwnerAndName - Matches by firstName case-insensitively")
    void searchByOwnerAndName_ByFirstName_ReturnsMatches() {
        Page<Contact> result = contactRepository.searchByOwnerAndName(
                owner, "ali", PageRequest.of(0, 10));
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getFirstName()).isEqualTo("Alice");
    }

    @Test
    @DisplayName("searchByOwnerAndName - Matches by lastName case-insensitively")
    void searchByOwnerAndName_ByLastName_ReturnsMatches() {
        Page<Contact> result = contactRepository.searchByOwnerAndName(
                owner, "johnson", PageRequest.of(0, 10));
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getFirstName()).isEqualTo("Bob");
    }

    @Test
    @DisplayName("searchByOwnerAndName - Does not return contacts from other users")
    void searchByOwnerAndName_DoesNotCrossUsers() {
        Page<Contact> result = contactRepository.searchByOwnerAndName(
                owner, "carol", PageRequest.of(0, 10));
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("countByOwner - Returns correct count for owner")
    void countByOwner_ReturnsCorrectCount() {
        long count = contactRepository.countByOwner(owner);
        assertThat(count).isEqualTo(2);
    }
}
