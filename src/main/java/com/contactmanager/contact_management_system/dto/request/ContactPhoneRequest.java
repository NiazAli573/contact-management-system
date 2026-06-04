package com.contactmanager.contact_management_system.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request DTO for a contact's phone number.
 */
@Data
public class ContactPhoneRequest {

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    /** WORK, HOME, MOBILE, OTHER */
    private String label;
}
