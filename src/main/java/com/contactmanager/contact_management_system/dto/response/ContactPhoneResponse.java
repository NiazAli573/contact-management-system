package com.contactmanager.contact_management_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for a contact's phone number.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactPhoneResponse {

    private Long id;
    private String phoneNumber;
    private String label;
}
