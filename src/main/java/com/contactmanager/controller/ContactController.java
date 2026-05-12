package com.contactmanager.controller;

import com.contactmanager.dto.ContactRequest;
import com.contactmanager.dto.ContactResponse;
import com.contactmanager.dto.ContactSummaryResponse;
import com.contactmanager.service.ContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @GetMapping
    public ResponseEntity<Page<ContactSummaryResponse>> getContacts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "") String search) {
        return ResponseEntity.ok(contactService.getContacts(page, size, search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactResponse> getContact(@PathVariable UUID id) {
        return ResponseEntity.ok(contactService.getContactById(id));
    }

    @PostMapping
    public ResponseEntity<ContactResponse> createContact(@Valid @RequestBody ContactRequest request) {
        ContactResponse response = contactService.createContact(request);
        return ResponseEntity.created(URI.create("/api/contacts/" + response.getId())).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContactResponse> updateContact(@PathVariable UUID id,
                                                         @Valid @RequestBody ContactRequest request) {
        return ResponseEntity.ok(contactService.updateContact(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable UUID id) {
        contactService.deleteContact(id);
        return ResponseEntity.noContent().build();
    }
}

