package com.contactmanager.contact_management_system.controller;

import com.contactmanager.contact_management_system.dto.request.ContactRequest;
import com.contactmanager.contact_management_system.dto.response.ContactResponse;
import com.contactmanager.contact_management_system.dto.response.PageResponse;
import com.contactmanager.contact_management_system.entity.User;
import com.contactmanager.contact_management_system.entity.enums.Role;
import com.contactmanager.contact_management_system.security.CustomUserDetailsService;
import com.contactmanager.contact_management_system.security.JwtAuthenticationFilter;
import com.contactmanager.contact_management_system.security.JwtTokenProvider;
import com.contactmanager.contact_management_system.security.SecurityConfig;
import com.contactmanager.contact_management_system.service.ContactService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * MockMvc controller tests for {@link ContactController}.
 */
@WebMvcTest(ContactController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtTokenProvider.class})
@org.springframework.test.context.ContextConfiguration(classes = com.contactmanager.contact_management_system.ContactManagementSystemApplication.class)
class ContactControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private ContactService contactService;
    @MockBean private CustomUserDetailsService customUserDetailsService;

    private User mockUser;
    private ContactResponse contactResponse;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .passwordHash("hash")
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build();

        contactResponse = ContactResponse.builder()
                .id(1L)
                .firstName("Alice")
                .lastName("Smith")
                .title("Engineer")
                .emails(List.of())
                .phones(List.of())
                .build();
    }

    @Test
    @DisplayName("GET /api/contacts - 200 OK with paginated results")
    @WithMockUser
    void getContacts_Returns200() throws Exception {
        PageResponse<ContactResponse> pageResponse = PageResponse.<ContactResponse>builder()
                .content(List.of(contactResponse))
                .page(0).size(10).totalElements(1).totalPages(1)
                .first(true).last(true)
                .build();

        when(contactService.getContacts(any(), anyInt(), anyInt(), any())).thenReturn(pageResponse);

        mockMvc.perform(get("/api/contacts")
                .with(user(mockUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].firstName").value("Alice"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("POST /api/contacts - 201 Created with valid body")
    @WithMockUser
    void createContact_ValidInput_Returns201() throws Exception {
        ContactRequest request = new ContactRequest();
        request.setFirstName("Alice");
        request.setLastName("Smith");

        when(contactService.createContact(any(), any(ContactRequest.class))).thenReturn(contactResponse);

        mockMvc.perform(post("/api/contacts")
                .with(user(mockUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Alice"));
    }

    @Test
    @DisplayName("POST /api/contacts - 400 Bad Request when firstName is blank")
    @WithMockUser
    void createContact_BlankFirstName_Returns400() throws Exception {
        ContactRequest request = new ContactRequest();
        request.setFirstName("");
        request.setLastName("Smith");

        mockMvc.perform(post("/api/contacts")
                .with(user(mockUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/contacts/{id} - 204 No Content on success")
    @WithMockUser
    void deleteContact_Returns204() throws Exception {
        doNothing().when(contactService).deleteContact(any(), eq(1L));

        mockMvc.perform(delete("/api/contacts/1")
                .with(user(mockUser)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/contacts - 401 Unauthorized without token")
    void getContacts_NoAuth_Returns401() throws Exception {
        mockMvc.perform(get("/api/contacts"))
                .andExpect(status().isUnauthorized());
    }
}
