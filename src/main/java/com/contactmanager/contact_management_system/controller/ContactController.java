package com.contactmanager.contact_management_system.controller;

import com.contactmanager.contact_management_system.dto.request.ContactRequest;
import com.contactmanager.contact_management_system.dto.response.ContactResponse;
import com.contactmanager.contact_management_system.dto.response.PageResponse;
import com.contactmanager.contact_management_system.entity.User;
import com.contactmanager.contact_management_system.service.ContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for contact management operations.
 */
@Slf4j
@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    /**
     * GET /api/contacts — paginated list of contacts, optionally filtered by search term.
     */
    @GetMapping
    public ResponseEntity<PageResponse<ContactResponse>> getContacts(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        log.info("GET /api/contacts — user: {}, page: {}, search: '{}'",
                currentUser.getEmail(), page, search);
        return ResponseEntity.ok(contactService.getContacts(currentUser, page, size, search));
    }

    /**
     * GET /api/contacts/{id} — retrieve a single contact by id.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ContactResponse> getContactById(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id) {
        log.info("GET /api/contacts/{} — user: {}", id, currentUser.getEmail());
        return ResponseEntity.ok(contactService.getContactById(currentUser, id));
    }

    /**
     * POST /api/contacts — create a new contact.
     */
    @PostMapping
    public ResponseEntity<ContactResponse> createContact(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody ContactRequest request) {
        log.info("POST /api/contacts — user: {}", currentUser.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(contactService.createContact(currentUser, request));
    }

    /**
     * PUT /api/contacts/{id} — update an existing contact.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ContactResponse> updateContact(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id,
            @Valid @RequestBody ContactRequest request) {
        log.info("PUT /api/contacts/{} — user: {}", id, currentUser.getEmail());
        return ResponseEntity.ok(contactService.updateContact(currentUser, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id) {
        log.info("DELETE /api/contacts/{} — user: {}", id, currentUser.getEmail());
        contactService.deleteContact(currentUser, id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/contacts/export — Export all contacts to a CSV file
     */
    @GetMapping(value = "/export", produces = "text/csv")
    public ResponseEntity<byte[]> exportContacts(@AuthenticationPrincipal User currentUser) {
        log.info("GET /api/contacts/export — user: {}", currentUser.getEmail());
        byte[] csvData = contactService.exportToCsv(currentUser);
        
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentDispositionFormData("attachment", "contacts.csv");
        headers.setContentType(org.springframework.http.MediaType.parseMediaType("text/csv"));
        
        return new ResponseEntity<>(csvData, headers, HttpStatus.OK);
    }

    /**
     * POST /api/contacts/import — Import contacts from a CSV file
     */
    @PostMapping(value = "/import", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> importContacts(
            @AuthenticationPrincipal User currentUser,
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        log.info("POST /api/contacts/import — user: {}", currentUser.getEmail());
        contactService.importFromCsv(currentUser, file);
        return ResponseEntity.ok().build();
    }
}
