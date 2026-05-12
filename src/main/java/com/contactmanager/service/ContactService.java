package com.contactmanager.service;

import com.contactmanager.dto.ContactRequest;
import com.contactmanager.dto.ContactResponse;
import com.contactmanager.dto.ContactSummaryResponse;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface ContactService {

    Page<ContactSummaryResponse> getContacts(int page, int size, String search);

    ContactResponse getContactById(UUID id);

    ContactResponse createContact(ContactRequest request);

    ContactResponse updateContact(UUID id, ContactRequest request);

    void deleteContact(UUID id);
}

