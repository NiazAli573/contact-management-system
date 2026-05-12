package com.contactmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactResponse {

    private UUID id;
    private String firstName;
    private String lastName;
    private String title;
    @Builder.Default
    private List<EmailDto> emails = new ArrayList<>();
    @Builder.Default
    private List<PhoneDto> phones = new ArrayList<>();
    private Instant createdAt;
    private Instant updatedAt;
}

