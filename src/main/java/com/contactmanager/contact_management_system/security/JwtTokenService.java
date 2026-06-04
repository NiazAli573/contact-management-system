package com.contactmanager.contact_management_system.security;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * Contract for JWT token generation and validation operations.
 */
public interface JwtTokenService {

    /**
     * Generates a JWT token for the given UserDetails principal.
     */
    String generateToken(UserDetails userDetails);

    /**
     * Extracts the username (subject) from a JWT token.
     */
    String extractUsername(String token);

    /**
     * Validates the token against the given UserDetails.
     */
    boolean isTokenValid(String token, UserDetails userDetails);

    /**
     * Returns true if the token can be parsed and has not expired.
     */
    boolean validateToken(String token);
}
