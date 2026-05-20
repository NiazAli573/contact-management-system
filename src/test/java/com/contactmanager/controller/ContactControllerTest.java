package com.contactmanager.controller;

import com.contactmanager.dto.ContactRequest;
import com.contactmanager.dto.ContactResponse;
import com.contactmanager.dto.ContactSummaryResponse;
import com.contactmanager.service.ContactService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContactController.class)
@DisplayName("ContactController Tests")
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ContactService contactService;

    @Test
    @DisplayName("GET /api/contacts: 200 returns paginated list with JWT")
    @WithMockUser(username = "john@example.com")
    void testGetContacts_WithAuth() throws Exception {
        UUID contactId = UUID.randomUUID();
        ContactSummaryResponse contact = ContactSummaryResponse.builder()
                .id(contactId)
                .firstName("Jane")
                .lastName("Smith")
                .title("Manager")
                .build();

        List<ContactSummaryResponse> contacts = List.of(contact);
        Page<ContactSummaryResponse> page = new PageImpl<>(contacts);

        when(contactService.getContacts(0, 10, "")).thenReturn(page);

        mockMvc.perform(get("/api/contacts")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].firstName").value("Jane"));
    }

    @Test
    @DisplayName("GET /api/contacts: 401 without JWT")
    void testGetContacts_NoAuth() throws Exception {
        mockMvc.perform(get("/api/contacts")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/contacts: 200 with search filter")
    @WithMockUser(username = "john@example.com")
    void testGetContacts_WithSearch() throws Exception {
        UUID contactId = UUID.randomUUID();
        ContactSummaryResponse contact = ContactSummaryResponse.builder()
                .id(contactId)
                .firstName("Jane")
                .lastName("Smith")
                .build();

        List<ContactSummaryResponse> contacts = List.of(contact);
        Page<ContactSummaryResponse> page = new PageImpl<>(contacts);

        when(contactService.getContacts(0, 10, "smith")).thenReturn(page);

        mockMvc.perform(get("/api/contacts")
                .param("page", "0")
                .param("size", "10")
                .param("search", "smith")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    @DisplayName("GET /api/contacts/{id}: 200 returns contact details")
    @WithMockUser(username = "john@example.com")
    void testGetContactById_Success() throws Exception {
        UUID contactId = UUID.randomUUID();
        ContactResponse contact = ContactResponse.builder()
                .id(contactId)
                .firstName("Jane")
                .lastName("Smith")
                .title("Manager")
                .emails(new ArrayList<>())
                .phones(new ArrayList<>())
                .build();

        when(contactService.getContactById(contactId)).thenReturn(contact);

        mockMvc.perform(get("/api/contacts/{id}", contactId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"));
    }

    @Test
    @DisplayName("GET /api/contacts/{id}: 404 when not found")
    @WithMockUser(username = "john@example.com")
    void testGetContactById_NotFound() throws Exception {
        UUID contactId = UUID.randomUUID();

        when(contactService.getContactById(contactId))
                .thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/api/contacts/{id}", contactId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/contacts: 201 with valid body")
    @WithMockUser(username = "john@example.com")
    void testCreateContact_Success() throws Exception {
        ContactRequest request = ContactRequest.builder()
                .firstName("Jane")
                .lastName("Smith")
                .title("Manager")
                .emails(new ArrayList<>())
                .phones(new ArrayList<>())
                .build();

        UUID contactId = UUID.randomUUID();
        ContactResponse response = ContactResponse.builder()
                .id(contactId)
                .firstName("Jane")
                .lastName("Smith")
                .title("Manager")
                .emails(new ArrayList<>())
                .phones(new ArrayList<>())
                .build();

        when(contactService.createContact(any(ContactRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/contacts")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    @DisplayName("POST /api/contacts: 400 with validation error (missing firstName)")
    @WithMockUser(username = "john@example.com")
    void testCreateContact_ValidationError() throws Exception {
        ContactRequest request = ContactRequest.builder()
                .lastName("Smith")
                .title("Manager")
                .build();

        mockMvc.perform(post("/api/contacts")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/contacts: 401 without authentication")
    void testCreateContact_NoAuth() throws Exception {
        ContactRequest request = ContactRequest.builder()
                .firstName("Jane")
                .lastName("Smith")
                .build();

        mockMvc.perform(post("/api/contacts")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PUT /api/contacts/{id}: 200 with valid body")
    @WithMockUser(username = "john@example.com")
    void testUpdateContact_Success() throws Exception {
        UUID contactId = UUID.randomUUID();
        ContactRequest request = ContactRequest.builder()
                .firstName("Jane")
                .lastName("Smith")
                .title("Senior Manager")
                .emails(new ArrayList<>())
                .phones(new ArrayList<>())
                .build();

        ContactResponse response = ContactResponse.builder()
                .id(contactId)
                .firstName("Jane")
                .lastName("Smith")
                .title("Senior Manager")
                .emails(new ArrayList<>())
                .phones(new ArrayList<>())
                .build();

        when(contactService.updateContact(eq(contactId), any(ContactRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/contacts/{id}", contactId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Senior Manager"));
    }

    @Test
    @DisplayName("PUT /api/contacts/{id}: 404 when not found")
    @WithMockUser(username = "john@example.com")
    void testUpdateContact_NotFound() throws Exception {
        UUID contactId = UUID.randomUUID();
        ContactRequest request = ContactRequest.builder()
                .firstName("Jane")
                .lastName("Smith")
                .build();

        when(contactService.updateContact(eq(contactId), any(ContactRequest.class)))
                .thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND));

        mockMvc.perform(put("/api/contacts/{id}", contactId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/contacts/{id}: 204 success")
    @WithMockUser(username = "john@example.com")
    void testDeleteContact_Success() throws Exception {
        UUID contactId = UUID.randomUUID();

        doNothing().when(contactService).deleteContact(contactId);

        mockMvc.perform(delete("/api/contacts/{id}", contactId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/contacts/{id}: 404 when not found")
    @WithMockUser(username = "john@example.com")
    void testDeleteContact_NotFound() throws Exception {
        UUID contactId = UUID.randomUUID();

        doThrow(new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND))
                .when(contactService).deleteContact(contactId);

        mockMvc.perform(delete("/api/contacts/{id}", contactId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/contacts/{id}: 401 without authentication")
    void testDeleteContact_NoAuth() throws Exception {
        UUID contactId = UUID.randomUUID();

        mockMvc.perform(delete("/api/contacts/{id}", contactId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}

