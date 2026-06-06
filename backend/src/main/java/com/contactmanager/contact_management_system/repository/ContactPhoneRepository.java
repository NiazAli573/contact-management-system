package com.contactmanager.contact_management_system.repository;

import com.contactmanager.contact_management_system.entity.ContactPhone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for ContactPhone entity operations.
 */
@Repository
public interface ContactPhoneRepository extends JpaRepository<ContactPhone, Long> {
}
