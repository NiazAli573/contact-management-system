package com.contactmanager.contact_management_system.entity;

import com.contactmanager.contact_management_system.entity.enums.PhoneLabel;
import jakarta.persistence.*;
import lombok.*;

/**
 * ContactPhone entity representing a phone number associated with a contact.
 */
@Entity
@Table(name = "contact_phones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactPhone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PhoneLabel label = PhoneLabel.MOBILE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Contact contact;
}
