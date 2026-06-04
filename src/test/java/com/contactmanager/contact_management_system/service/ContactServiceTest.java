package com.contactmanager.contact_management_system.service;

import com.contactmanager.contact_management_system.dto.request.ContactRequest;
import com.contactmanager.contact_management_system.dto.response.ContactResponse;
import com.contactmanager.contact_management_system.dto.response.PageResponse;
import com.contactmanager.contact_management_system.entity.Contact;
import com.contactmanager.contact_management_system.entity.User;
import com.contactmanager.contact_management_system.entity.enums.Role;
import com.contactmanager.contact_management_system.exception.ResourceNotFoundException;
import com.contactmanager.contact_management_system.repository.ContactRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ContactService}.
 */
@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

    @Mock private ContactRepository contactRepository;

    @InjectMocks private ContactService contactService;

    private User owner;
    private Contact contact;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .passwordHash("hash")
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build();

        contact = Contact.builder()
                .id(10L)
                .firstName("Alice")
                .lastName("Smith")
                .title("Engineer")
                .owner(owner)
                .emails(new ArrayList<>())
                .phones(new ArrayList<>())
                .build();
    }

    // ─── Get Contacts ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("GetContacts - Returns paginated list without search")
    void getContacts_NoSearch_ReturnsPaginatedList() {
        Page<Contact> page = new PageImpl<>(List.of(contact), PageRequest.of(0, 10), 1);
        when(contactRepository.findByOwner(eq(owner), any(Pageable.class))).thenReturn(page);

        PageResponse<ContactResponse> result = contactService.getContacts(owner, 0, 10, null);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getFirstName()).isEqualTo("Alice");
    }

    @Test
    @DisplayName("GetContacts - Calls search query when search term is provided")
    void getContacts_WithSearch_CallsSearchQuery() {
        Page<Contact> page = new PageImpl<>(List.of(contact), PageRequest.of(0, 10), 1);
        when(contactRepository.searchByOwnerAndName(eq(owner), eq("Alice"), any(Pageable.class)))
                .thenReturn(page);

        PageResponse<ContactResponse> result = contactService.getContacts(owner, 0, 10, "Alice");

        assertThat(result.getContent()).hasSize(1);
        verify(contactRepository).searchByOwnerAndName(eq(owner), eq("Alice"), any(Pageable.class));
        verify(contactRepository, never()).findByOwner(any(), any());
    }

    // ─── Get By ID ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GetContactById - Returns contact when owned by user")
    void getContactById_Found_ReturnsResponse() {
        when(contactRepository.findById(10L)).thenReturn(Optional.of(contact));

        ContactResponse result = contactService.getContactById(owner, 10L);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getFirstName()).isEqualTo("Alice");
    }

    @Test
    @DisplayName("GetContactById - Throws ResourceNotFoundException for wrong owner")
    void getContactById_WrongOwner_ThrowsException() {
        User anotherUser = User.builder().id(99L).email("other@example.com").build();
        when(contactRepository.findById(10L)).thenReturn(Optional.of(contact));

        assertThatThrownBy(() -> contactService.getContactById(anotherUser, 10L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("GetContactById - Throws ResourceNotFoundException when not found")
    void getContactById_NotFound_ThrowsException() {
        when(contactRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contactService.getContactById(owner, 999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── Create ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("CreateContact - Saves and returns new contact")
    void createContact_Success() {
        ContactRequest request = new ContactRequest();
        request.setFirstName("Bob");
        request.setLastName("Jones");
        request.setTitle("Manager");

        Contact newContact = Contact.builder()
                .id(11L)
                .firstName("Bob")
                .lastName("Jones")
                .title("Manager")
                .owner(owner)
                .emails(new ArrayList<>())
                .phones(new ArrayList<>())
                .build();

        when(contactRepository.save(any(Contact.class))).thenReturn(newContact);

        ContactResponse result = contactService.createContact(owner, request);

        assertThat(result.getFirstName()).isEqualTo("Bob");
        assertThat(result.getId()).isEqualTo(11L);
        verify(contactRepository).save(any(Contact.class));
    }

    // ─── Update ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("UpdateContact - Updates and returns modified contact")
    void updateContact_Success() {
        ContactRequest request = new ContactRequest();
        request.setFirstName("Alice");
        request.setLastName("Johnson");
        request.setTitle("Senior Engineer");

        when(contactRepository.findById(10L)).thenReturn(Optional.of(contact));
        when(contactRepository.save(any(Contact.class))).thenAnswer(inv -> inv.getArgument(0));

        ContactResponse result = contactService.updateContact(owner, 10L, request);

        assertThat(result.getLastName()).isEqualTo("Johnson");
        assertThat(result.getTitle()).isEqualTo("Senior Engineer");
    }

    // ─── Delete ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("DeleteContact - Deletes contact owned by user")
    void deleteContact_Success() {
        when(contactRepository.findById(10L)).thenReturn(Optional.of(contact));

        assertThatCode(() -> contactService.deleteContact(owner, 10L))
                .doesNotThrowAnyException();

        verify(contactRepository).delete(contact);
    }

    @Test
    @DisplayName("DeleteContact - Throws ResourceNotFoundException when not found")
    void deleteContact_NotFound_ThrowsException() {
        when(contactRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contactService.deleteContact(owner, 999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
