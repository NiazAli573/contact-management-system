package com.contactmanager.contact_management_system.service;

import com.contactmanager.contact_management_system.dto.request.ContactEmailRequest;
import com.contactmanager.contact_management_system.dto.request.ContactPhoneRequest;
import com.contactmanager.contact_management_system.dto.request.ContactRequest;
import com.contactmanager.contact_management_system.dto.response.ContactEmailResponse;
import com.contactmanager.contact_management_system.dto.response.ContactPhoneResponse;
import com.contactmanager.contact_management_system.dto.response.ContactResponse;
import com.contactmanager.contact_management_system.dto.response.PageResponse;
import com.contactmanager.contact_management_system.entity.Contact;
import com.contactmanager.contact_management_system.entity.ContactEmail;
import com.contactmanager.contact_management_system.entity.ContactPhone;
import com.contactmanager.contact_management_system.entity.User;
import com.contactmanager.contact_management_system.entity.enums.EmailLabel;
import com.contactmanager.contact_management_system.entity.enums.PhoneLabel;
import com.contactmanager.contact_management_system.exception.ResourceNotFoundException;
import com.contactmanager.contact_management_system.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for contact CRUD operations with pagination and search.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;

    // ─── Read ─────────────────────────────────────────────────────────────────

    /**
     * Returns a paginated, optionally-searched list of contacts owned by the user.
     */
    @Transactional(readOnly = true)
    public PageResponse<ContactResponse> getContacts(User owner, int page, int size, String search) {
        log.debug("Fetching contacts for user '{}', page={}, size={}, search='{}'",
                owner.getEmail(), page, size, search);

        Pageable pageable = PageRequest.of(page, size, Sort.by("firstName", "lastName"));
        Page<Contact> contactPage;

        if (StringUtils.hasText(search)) {
            contactPage = contactRepository.searchByOwnerAndName(owner, search, pageable);
        } else {
            contactPage = contactRepository.findByOwner(owner, pageable);
        }

        List<ContactResponse> content = contactPage.getContent()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return PageResponse.<ContactResponse>builder()
                .content(content)
                .page(contactPage.getNumber())
                .size(contactPage.getSize())
                .totalElements(contactPage.getTotalElements())
                .totalPages(contactPage.getTotalPages())
                .last(contactPage.isLast())
                .first(contactPage.isFirst())
                .build();
    }

    /**
     * Returns a single contact by id, verifying ownership.
     */
    @Transactional(readOnly = true)
    public ContactResponse getContactById(User owner, Long contactId) {
        log.debug("Fetching contact id={} for user '{}'", contactId, owner.getEmail());
        Contact contact = findContactByIdAndOwner(contactId, owner);
        return toResponse(contact);
    }

    // ─── Create ───────────────────────────────────────────────────────────────

    @Transactional
    public ContactResponse createContact(User owner, ContactRequest request) {
        log.info("Creating contact '{}  {}' for user '{}'",
                request.getFirstName(), request.getLastName(), owner.getEmail());

        Contact contact = Contact.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .title(request.getTitle())
                .owner(owner)
                .build();

        addEmailsToContact(contact, request.getEmails());
        addPhonesToContact(contact, request.getPhones());

        Contact saved = contactRepository.save(contact);
        log.info("Contact created with id={}", saved.getId());
        return toResponse(saved);
    }

    // ─── Update ───────────────────────────────────────────────────────────────

    @Transactional
    public ContactResponse updateContact(User owner, Long contactId, ContactRequest request) {
        log.info("Updating contact id={} for user '{}'", contactId, owner.getEmail());

        Contact contact = findContactByIdAndOwner(contactId, owner);

        contact.setFirstName(request.getFirstName());
        contact.setLastName(request.getLastName());
        contact.setTitle(request.getTitle());

        contact.getEmails().clear();
        contact.getPhones().clear();

        addEmailsToContact(contact, request.getEmails());
        addPhonesToContact(contact, request.getPhones());

        Contact updated = contactRepository.save(contact);
        log.info("Contact id={} updated successfully", updated.getId());
        return toResponse(updated);
    }

    // ─── Delete ───────────────────────────────────────────────────────────────

    @Transactional
    public void deleteContact(User owner, Long contactId) {
        log.info("Deleting contact id={} for user '{}'", contactId, owner.getEmail());
        Contact contact = findContactByIdAndOwner(contactId, owner);
        contactRepository.delete(contact);
        log.info("Contact id={} deleted successfully", contactId);
    }

    // ─── Private Helpers ──────────────────────────────────────────────────────

    private Contact findContactByIdAndOwner(Long contactId, User owner) {
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact", "id", contactId));
        if (!contact.getOwner().getId().equals(owner.getId())) {
            log.warn("Unauthorized access attempt to contact id={} by user '{}'",
                    contactId, owner.getEmail());
            throw new ResourceNotFoundException("Contact", "id", contactId);
        }
        return contact;
    }

    private void addEmailsToContact(Contact contact, List<ContactEmailRequest> emailRequests) {
        if (emailRequests == null) return;
        emailRequests.forEach(req -> {
            EmailLabel label;
            try {
                label = EmailLabel.valueOf(req.getLabel() != null ? req.getLabel().toUpperCase() : "PERSONAL");
            } catch (IllegalArgumentException e) {
                label = EmailLabel.PERSONAL;
            }
            ContactEmail email = ContactEmail.builder()
                    .email(req.getEmail())
                    .label(label)
                    .contact(contact)
                    .build();
            contact.getEmails().add(email);
        });
    }

    private void addPhonesToContact(Contact contact, List<ContactPhoneRequest> phoneRequests) {
        if (phoneRequests == null) return;
        phoneRequests.forEach(req -> {
            PhoneLabel label;
            try {
                label = PhoneLabel.valueOf(req.getLabel() != null ? req.getLabel().toUpperCase() : "MOBILE");
            } catch (IllegalArgumentException e) {
                label = PhoneLabel.MOBILE;
            }
            ContactPhone phone = ContactPhone.builder()
                    .phoneNumber(req.getPhoneNumber())
                    .label(label)
                    .contact(contact)
                    .build();
            contact.getPhones().add(phone);
        });
    }

    public ContactResponse toResponse(Contact contact) {
        List<ContactEmailResponse> emails = contact.getEmails().stream()
                .map(e -> ContactEmailResponse.builder()
                        .id(e.getId())
                        .email(e.getEmail())
                        .label(e.getLabel().name())
                        .build())
                .collect(Collectors.toList());

        List<ContactPhoneResponse> phones = contact.getPhones().stream()
                .map(p -> ContactPhoneResponse.builder()
                        .id(p.getId())
                        .phoneNumber(p.getPhoneNumber())
                        .label(p.getLabel().name())
                        .build())
                .collect(Collectors.toList());

        return ContactResponse.builder()
                .id(contact.getId())
                .firstName(contact.getFirstName())
                .lastName(contact.getLastName())
                .title(contact.getTitle())
                .emails(emails)
                .phones(phones)
                .build();
    }
}
