package com.contactmanager.service;

import com.contactmanager.dto.ContactRequest;
import com.contactmanager.dto.ContactResponse;
import com.contactmanager.dto.ContactSummaryResponse;
import com.contactmanager.entity.Contact;
import com.contactmanager.entity.User;
import com.contactmanager.mapper.ContactMapper;
import com.contactmanager.repository.ContactRepository;
import com.contactmanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ContactServiceImpl Tests")
class ContactServiceImplTest {

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ContactServiceImpl contactService;

    private User testUser;
    private Contact testContact;
    private ContactRequest contactRequest;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .password("hashedPassword")
                .build();

        testContact = Contact.builder()
                .id(UUID.randomUUID())
                .firstName("Jane")
                .lastName("Smith")
                .title("Manager")
                .owner(testUser)
                .emails(new ArrayList<>())
                .phones(new ArrayList<>())
                .build();

        contactRequest = ContactRequest.builder()
                .firstName("Jane")
                .lastName("Smith")
                .title("Manager")
                .emails(new ArrayList<>())
                .phones(new ArrayList<>())
                .build();

        authentication = new UsernamePasswordAuthenticationToken(
                testUser.getEmail(), null, new ArrayList<>()
        );

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("CreateContact: saves and returns ContactResponse correctly")
    void testCreateContact_Success() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(contactRepository.save(any(Contact.class))).thenReturn(testContact);

        ContactResponse response = contactService.createContact(contactRequest);

        assertNotNull(response);
        assertEquals(testContact.getFirstName(), response.getFirstName());
        assertEquals(testContact.getLastName(), response.getLastName());
        verify(contactRepository, times(1)).save(any(Contact.class));
    }

    @Test
    @DisplayName("GetContacts: returns paginated results filtered by search term")
    void testGetContacts_WithSearch() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Contact> contacts = List.of(testContact);
        Page<Contact> page = new PageImpl<>(contacts, pageable, 1);

        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(contactRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);

        Page<ContactSummaryResponse> response = contactService.getContacts(0, 10, "smith");

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        verify(contactRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("GetContacts: returns paginated results without search")
    void testGetContacts_NoSearch() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Contact> contacts = List.of(testContact);
        Page<Contact> page = new PageImpl<>(contacts, pageable, 1);

        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(contactRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);

        Page<ContactSummaryResponse> response = contactService.getContacts(0, 10, "");

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
    }

    @Test
    @DisplayName("GetContactById: success")
    void testGetContactById_Success() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(contactRepository.findDetailedById(testContact.getId())).thenReturn(Optional.of(testContact));

        ContactResponse response = contactService.getContactById(testContact.getId());

        assertNotNull(response);
        assertEquals(testContact.getFirstName(), response.getFirstName());
    }

    @Test
    @DisplayName("GetContactById: not found throws 404")
    void testGetContactById_NotFound() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(contactRepository.findDetailedById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> contactService.getContactById(UUID.randomUUID()));
    }

    @Test
    @DisplayName("GetContactById: wrong owner throws 403")
    void testGetContactById_WrongOwner() {
        User otherUser = User.builder()
                .id(UUID.randomUUID())
                .firstName("Other")
                .lastName("User")
                .email("other@example.com")
                .build();

        Contact otherUserContact = Contact.builder()
                .id(UUID.randomUUID())
                .firstName("Jane")
                .lastName("Smith")
                .owner(otherUser)
                .build();

        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(contactRepository.findDetailedById(otherUserContact.getId())).thenReturn(Optional.of(otherUserContact));

        assertThrows(ResponseStatusException.class, () -> contactService.getContactById(otherUserContact.getId()));
    }

    @Test
    @DisplayName("UpdateContact: success")
    void testUpdateContact_Success() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(contactRepository.findDetailedById(testContact.getId())).thenReturn(Optional.of(testContact));
        when(contactRepository.save(any(Contact.class))).thenReturn(testContact);

        ContactResponse response = contactService.updateContact(testContact.getId(), contactRequest);

        assertNotNull(response);
        verify(contactRepository, times(1)).save(any(Contact.class));
    }

    @Test
    @DisplayName("UpdateContact: not found throws 404")
    void testUpdateContact_NotFound() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(contactRepository.findDetailedById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> contactService.updateContact(UUID.randomUUID(), contactRequest));
    }

    @Test
    @DisplayName("UpdateContact: wrong owner throws 403")
    void testUpdateContact_WrongOwner() {
        User otherUser = User.builder()
                .id(UUID.randomUUID())
                .firstName("Other")
                .lastName("User")
                .email("other@example.com")
                .build();

        Contact otherUserContact = Contact.builder()
                .id(UUID.randomUUID())
                .firstName("Jane")
                .lastName("Smith")
                .owner(otherUser)
                .build();

        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(contactRepository.findDetailedById(otherUserContact.getId())).thenReturn(Optional.of(otherUserContact));

        assertThrows(ResponseStatusException.class, () -> contactService.updateContact(otherUserContact.getId(), contactRequest));
    }

    @Test
    @DisplayName("DeleteContact: success")
    void testDeleteContact_Success() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(contactRepository.findDetailedById(testContact.getId())).thenReturn(Optional.of(testContact));

        assertDoesNotThrow(() -> contactService.deleteContact(testContact.getId()));
        verify(contactRepository, times(1)).delete(testContact);
    }

    @Test
    @DisplayName("DeleteContact: not found throws 404")
    void testDeleteContact_NotFound() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(contactRepository.findDetailedById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> contactService.deleteContact(UUID.randomUUID()));
    }

    @Test
    @DisplayName("DeleteContact: wrong owner throws 403")
    void testDeleteContact_WrongOwner() {
        User otherUser = User.builder()
                .id(UUID.randomUUID())
                .firstName("Other")
                .lastName("User")
                .email("other@example.com")
                .build();

        Contact otherUserContact = Contact.builder()
                .id(UUID.randomUUID())
                .firstName("Jane")
                .lastName("Smith")
                .owner(otherUser)
                .build();

        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(contactRepository.findDetailedById(otherUserContact.getId())).thenReturn(Optional.of(otherUserContact));

        assertThrows(ResponseStatusException.class, () -> contactService.deleteContact(otherUserContact.getId()));
    }
}

