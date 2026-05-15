package com.contactmanager.service;

import com.contactmanager.dto.ContactRequest;
import com.contactmanager.dto.ContactResponse;
import com.contactmanager.dto.ContactSummaryResponse;
import com.contactmanager.entity.Contact;
import com.contactmanager.entity.User;
import com.contactmanager.mapper.ContactMapper;
import com.contactmanager.repository.ContactRepository;
import com.contactmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<ContactSummaryResponse> getContacts(int page, int size, String search) {
        User currentUser = getCurrentUser();
        Pageable pageable = PageRequest.of(page, size);
        String normalizedSearch = normalizeSearch(search);

        Specification<Contact> specification = Specification.where(ownedBy(currentUser.getEmail()))
                .and(matchesSearch(normalizedSearch));

        return contactRepository.findAll(specification, pageable)
                .map(ContactMapper::toSummaryResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ContactResponse getContactById(UUID id) {
        Contact contact = getOwnedContactOrThrow(id);
        return ContactMapper.toResponse(contact);
    }

    @Override
    @Transactional
    public ContactResponse createContact(ContactRequest request) {
        User currentUser = getCurrentUser();
        Contact contact = ContactMapper.toEntity(request, currentUser);
        Contact saved = contactRepository.save(contact);
        return ContactMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ContactResponse updateContact(UUID id, ContactRequest request) {
        Contact contact = getOwnedContactOrThrow(id);
        ContactMapper.applyRequest(contact, request);
        contact.getEmails().clear();
        contact.getPhones().clear();
        contact.getEmails().addAll(ContactMapper.mapEmails(request.getEmails(), contact));
        contact.getPhones().addAll(ContactMapper.mapPhones(request.getPhones(), contact));
        Contact saved = contactRepository.save(contact);
        return ContactMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteContact(UUID id) {
        log.debug("deleteContact() entry with id={}", id);

        Contact contact = getOwnedContactOrThrow(id);
        contactRepository.delete(contact);

        log.info("Contact deleted successfully - id={}", id);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(UNAUTHORIZED, "Authentication required");
        }

        String email = authentication.getName();
        if (!StringUtils.hasText(email) || "anonymousUser".equals(email)) {
            throw new ResponseStatusException(UNAUTHORIZED, "Authentication required");
        }

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Authenticated user not found"));
    }

    private Contact getOwnedContactOrThrow(UUID id) {
        Contact contact = contactRepository.findDetailedById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Contact not found"));

        User currentUser = getCurrentUser();
        if (contact.getOwner() == null || contact.getOwner().getEmail() == null
                || !contact.getOwner().getEmail().equals(currentUser.getEmail())) {
            throw new ResponseStatusException(FORBIDDEN, "You are not allowed to access this contact");
        }

        return contact;
    }

    private Specification<Contact> ownedBy(String ownerEmail) {
        return (root, query, cb) -> cb.equal(root.join("owner").get("email"), ownerEmail);
    }

    private Specification<Contact> matchesSearch(String search) {
        if (!StringUtils.hasText(search)) {
            return Specification.where(null);
        }

        return (root, query, cb) -> {
            String pattern = "%" + search.toLowerCase() + "%";
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.like(cb.lower(root.get("firstName")), pattern));
            predicates.add(cb.like(cb.lower(root.get("lastName")), pattern));
            return cb.or(predicates.toArray(new Predicate[0]));
        };
    }

    private String normalizeSearch(String search) {
        return search == null ? null : search.trim();
    }
}

