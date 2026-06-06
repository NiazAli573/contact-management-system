package com.contactmanager.contact_management_system.repository;

import com.contactmanager.contact_management_system.entity.Contact;
import com.contactmanager.contact_management_system.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository for Contact entity operations with search support.
 */
@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    /**
     * Returns all contacts belonging to a given owner.
     */
    Page<Contact> findByOwner(User owner, Pageable pageable);

    /**
     * Searches contacts for a given owner by first name or last name (case-insensitive).
     */
    @Query("SELECT c FROM Contact c WHERE c.owner = :owner AND " +
           "(LOWER(c.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Contact> searchByOwnerAndName(
            @Param("owner") User owner,
            @Param("search") String search,
            Pageable pageable
    );

    /**
     * Count contacts by owner.
     */
    long countByOwner(User owner);
}
