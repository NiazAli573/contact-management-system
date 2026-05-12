package com.contactmanager.repository;

import com.contactmanager.entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ContactRepository extends JpaRepository<Contact, UUID>, JpaSpecificationExecutor<Contact> {

	@EntityGraph(attributePaths = {"owner", "emails", "phones"})
	@Query("select c from Contact c where c.id = :id")
	Optional<Contact> findDetailedById(@Param("id") UUID id);

	Page<Contact> findByOwner_Email(String ownerEmail, Pageable pageable);
}

