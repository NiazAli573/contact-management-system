package com.contactmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactSummaryResponse {

    private UUID id;
    private String firstName;
    private String lastName;
    private String title;
    private long emailCount;
    private long phoneCount;
    private Instant createdAt;
    private Instant updatedAt;
}

