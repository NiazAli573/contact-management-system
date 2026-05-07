package com.contactmanager.repository;

import com.contactmanager.entity.ContactEmail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ContactEmailRepository extends JpaRepository<ContactEmail, UUID> {
}

