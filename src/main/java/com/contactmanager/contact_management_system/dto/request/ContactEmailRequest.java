package com.contactmanager.contact_management_system.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request DTO for a contact's email address.
 */
@Data
public class ContactEmailRequest {

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    /** WORK, PERSONAL, OTHER */
    private String label;
}
