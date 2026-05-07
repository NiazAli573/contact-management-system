package com.contactmanager.repository;

import com.contactmanager.entity.ContactPhone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ContactPhoneRepository extends JpaRepository<ContactPhone, UUID> {
}

