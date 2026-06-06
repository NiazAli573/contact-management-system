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

    // ─── Email and Phone Edge Cases ──────────────────────────────────────────

    @Test
    @DisplayName("CreateContact - Handles null emails and phones")
    void createContact_NullCollections() {
        ContactRequest request = new ContactRequest();
        request.setFirstName("No");
        request.setLastName("Collections");
        request.setEmails(null);
        request.setPhones(null);

        Contact newContact = Contact.builder().id(12L).owner(owner)
                .emails(new ArrayList<>()).phones(new ArrayList<>()).build();
        when(contactRepository.save(any(Contact.class))).thenReturn(newContact);

        ContactResponse response = contactService.createContact(owner, request);

        assertThat(response.getEmails()).isEmpty();
        assertThat(response.getPhones()).isEmpty();
        verify(contactRepository).save(argThat(c -> c.getEmails().isEmpty() && c.getPhones().isEmpty()));
    }

    @Test
    @DisplayName("CreateContact - Handles valid and invalid labels, falling back to default")
    void createContact_LabelHandling() {
        ContactRequest request = new ContactRequest();
        request.setFirstName("Labels");
        request.setLastName("Test");

        com.contactmanager.contact_management_system.dto.request.ContactEmailRequest emailReq1 = new com.contactmanager.contact_management_system.dto.request.ContactEmailRequest();
        emailReq1.setEmail("work@example.com");
        emailReq1.setLabel("WORK");

        com.contactmanager.contact_management_system.dto.request.ContactEmailRequest emailReq2 = new com.contactmanager.contact_management_system.dto.request.ContactEmailRequest();
        emailReq2.setEmail("invalid@example.com");
        emailReq2.setLabel("INVALID_LABEL");

        request.setEmails(List.of(emailReq1, emailReq2));

        com.contactmanager.contact_management_system.dto.request.ContactPhoneRequest phoneReq1 = new com.contactmanager.contact_management_system.dto.request.ContactPhoneRequest();
        phoneReq1.setPhoneNumber("12345");
        phoneReq1.setLabel("HOME");

        com.contactmanager.contact_management_system.dto.request.ContactPhoneRequest phoneReq2 = new com.contactmanager.contact_management_system.dto.request.ContactPhoneRequest();
        phoneReq2.setPhoneNumber("99999");
        phoneReq2.setLabel("STRANGE_LABEL");

        request.setPhones(List.of(phoneReq1, phoneReq2));

        when(contactRepository.save(any(Contact.class))).thenAnswer(inv -> inv.getArgument(0));

        ContactResponse response = contactService.createContact(owner, request);

        assertThat(response.getEmails()).hasSize(2);
        assertThat(response.getEmails().get(0).getLabel()).isEqualTo("WORK");
        assertThat(response.getEmails().get(1).getLabel()).isEqualTo("PERSONAL"); // Fallback

        assertThat(response.getPhones()).hasSize(2);
        assertThat(response.getPhones().get(0).getLabel()).isEqualTo("HOME");
        assertThat(response.getPhones().get(1).getLabel()).isEqualTo("MOBILE"); // Fallback
    }

    // ─── Export/Import CSV ───────────────────────────────────────────────────

    @Test
    @DisplayName("ExportToCsv - Handles empty contacts list")
    void exportToCsv_EmptyList() {
        when(contactRepository.findByOwner(eq(owner), any(Pageable.class))).thenReturn(new PageImpl<>(new ArrayList<>()));

        byte[] csvBytes = contactService.exportToCsv(owner);
        String csvString = new String(csvBytes);

        assertThat(csvString).contains("FirstName", "LastName", "Title", "Emails", "Phones");
    }

    @Test
    @DisplayName("ExportToCsv - Exports multiple contacts correctly")
    void exportToCsv_MultipleContacts() {
        Contact contact2 = Contact.builder()
                .id(11L)
                .firstName("Bob")
                .lastName("Smith")
                .owner(owner)
                .emails(List.of(
                        com.contactmanager.contact_management_system.entity.ContactEmail.builder()
                                .email("bob@work.com").label(com.contactmanager.contact_management_system.entity.enums.EmailLabel.WORK).build()
                ))
                .phones(List.of(
                        com.contactmanager.contact_management_system.entity.ContactPhone.builder()
                                .phoneNumber("555-1234").label(com.contactmanager.contact_management_system.entity.enums.PhoneLabel.MOBILE).build()
                ))
                .build();

        when(contactRepository.findByOwner(eq(owner), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(contact, contact2)));

        byte[] csvBytes = contactService.exportToCsv(owner);
        String csvString = new String(csvBytes);

        assertThat(csvString).contains("Alice");
        assertThat(csvString).contains("Bob");
        assertThat(csvString).contains("bob@work.com:WORK");
        assertThat(csvString).contains("555-1234:MOBILE");
    }

    @Test
    @DisplayName("ImportFromCsv - Successfully imports contacts")
    void importFromCsv_Success() throws Exception {
        String csvContent = "FirstName,LastName,Title,Emails,Phones\n" +
                "Imported,User,Dev,test@example.com:PERSONAL,12345:WORK\n" +
                "Invalid,Row\n"; // Missing fields should be skipped or handled safely
                
        org.springframework.mock.web.MockMultipartFile file = new org.springframework.mock.web.MockMultipartFile(
                "file", "contacts.csv", "text/csv", csvContent.getBytes());

        contactService.importFromCsv(owner, file);

        verify(contactRepository, times(2)).save(any(Contact.class));
    }

    @Test
    @DisplayName("ImportFromCsv - Handles empty file")
    void importFromCsv_EmptyFile() throws Exception {
        org.springframework.mock.web.MockMultipartFile file = new org.springframework.mock.web.MockMultipartFile(
                "file", "contacts.csv", "text/csv", new byte[0]);

        contactService.importFromCsv(owner, file);

        verify(contactRepository, never()).save(any(Contact.class));
    }
}
