package com.contactmanager.dto;

import com.contactmanager.entity.ContactPhoneLabel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhoneDto {

    private UUID id;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Phone number must be valid")
    private String phoneNumber;

    @NotNull(message = "Phone label is required")
    private ContactPhoneLabel label;
}


