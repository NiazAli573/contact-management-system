package com.contactmanager.contact_management_system.entity;

import com.contactmanager.contact_management_system.entity.enums.EmailLabel;
import jakarta.persistence.*;
import lombok.*;

/**
 * ContactEmail entity representing an email address associated with a contact.
 */
@Entity
@Table(name = "contact_emails")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactEmail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EmailLabel label = EmailLabel.PERSONAL;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Contact contact;
}
