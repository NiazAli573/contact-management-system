package com.contactmanager.repository;

import com.contactmanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
	Optional<User> findByEmail(String email);

	boolean existsByEmail(String email);

	boolean existsByPhoneNumber(String phoneNumber);
}

