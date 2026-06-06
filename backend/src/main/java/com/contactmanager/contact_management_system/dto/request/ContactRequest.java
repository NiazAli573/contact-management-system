package com.contactmanager.contact_management_system.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Request DTO for creating or updating a contact.
 */
@Data
public class ContactRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private String title;

    @Valid
    private List<ContactEmailRequest> emails = new ArrayList<>();

    @Valid
    private List<ContactPhoneRequest> phones = new ArrayList<>();
}
