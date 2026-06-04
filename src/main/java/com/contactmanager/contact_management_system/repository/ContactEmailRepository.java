package com.contactmanager.contact_management_system.repository;

import com.contactmanager.contact_management_system.entity.ContactEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for ContactEmail entity operations.
 */
@Repository
public interface ContactEmailRepository extends JpaRepository<ContactEmail, Long> {
}
