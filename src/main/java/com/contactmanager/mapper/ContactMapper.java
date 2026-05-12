package com.contactmanager.mapper;

import com.contactmanager.dto.ContactRequest;
import com.contactmanager.dto.ContactResponse;
import com.contactmanager.dto.ContactSummaryResponse;
import com.contactmanager.dto.EmailDto;
import com.contactmanager.dto.PhoneDto;
import com.contactmanager.entity.Contact;
import com.contactmanager.entity.ContactEmail;
import com.contactmanager.entity.ContactPhone;
import com.contactmanager.entity.User;

import java.util.ArrayList;
import java.util.List;

public final class ContactMapper {

    private ContactMapper() {
    }

    public static Contact toEntity(ContactRequest request, User owner) {
        Contact contact = Contact.builder()
                .firstName(normalize(request.getFirstName()))
                .lastName(normalize(request.getLastName()))
                .title(normalize(request.getTitle()))
                .owner(owner)
                .build();

        contact.setEmails(mapEmails(request.getEmails(), contact));
        contact.setPhones(mapPhones(request.getPhones(), contact));
        return contact;
    }

    public static ContactResponse toResponse(Contact contact) {
        return ContactResponse.builder()
                .id(contact.getId())
                .firstName(contact.getFirstName())
                .lastName(contact.getLastName())
                .title(contact.getTitle())
                .emails(mapEmailDtos(contact.getEmails()))
                .phones(mapPhoneDtos(contact.getPhones()))
                .createdAt(contact.getCreatedAt())
                .updatedAt(contact.getUpdatedAt())
                .build();
    }

    public static ContactSummaryResponse toSummaryResponse(Contact contact) {
        return ContactSummaryResponse.builder()
                .id(contact.getId())
                .firstName(contact.getFirstName())
                .lastName(contact.getLastName())
                .title(contact.getTitle())
                .emailCount(contact.getEmails() == null ? 0 : contact.getEmails().size())
                .phoneCount(contact.getPhones() == null ? 0 : contact.getPhones().size())
                .createdAt(contact.getCreatedAt())
                .updatedAt(contact.getUpdatedAt())
                .build();
    }

    public static void applyRequest(Contact contact, ContactRequest request) {
        contact.setFirstName(normalize(request.getFirstName()));
        contact.setLastName(normalize(request.getLastName()));
        contact.setTitle(normalize(request.getTitle()));
    }

    public static List<ContactEmail> mapEmails(List<EmailDto> dtos, Contact contact) {
        List<ContactEmail> emails = new ArrayList<>();
        if (dtos == null) {
            return emails;
        }
        for (EmailDto dto : dtos) {
            emails.add(ContactEmail.builder()
                    .email(normalize(dto.getEmail()))
                    .label(dto.getLabel())
                    .contact(contact)
                    .build());
        }
        return emails;
    }

    public static List<ContactPhone> mapPhones(List<PhoneDto> dtos, Contact contact) {
        List<ContactPhone> phones = new ArrayList<>();
        if (dtos == null) {
            return phones;
        }
        for (PhoneDto dto : dtos) {
            phones.add(ContactPhone.builder()
                    .phoneNumber(normalize(dto.getPhoneNumber()))
                    .label(dto.getLabel())
                    .contact(contact)
                    .build());
        }
        return phones;
    }

    private static List<EmailDto> mapEmailDtos(List<ContactEmail> emails) {
        List<EmailDto> result = new ArrayList<>();
        if (emails == null) {
            return result;
        }
        for (ContactEmail email : emails) {
            result.add(EmailDto.builder()
                    .id(email.getId())
                    .email(email.getEmail())
                    .label(email.getLabel())
                    .build());
        }
        return result;
    }

    private static List<PhoneDto> mapPhoneDtos(List<ContactPhone> phones) {
        List<PhoneDto> result = new ArrayList<>();
        if (phones == null) {
            return result;
        }
        for (ContactPhone phone : phones) {
            result.add(PhoneDto.builder()
                    .id(phone.getId())
                    .phoneNumber(phone.getPhoneNumber())
                    .label(phone.getLabel())
                    .build());
        }
        return result;
    }

    private static String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}


