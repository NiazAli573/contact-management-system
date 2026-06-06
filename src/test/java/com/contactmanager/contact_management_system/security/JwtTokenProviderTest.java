package com.contactmanager.contact_management_system.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private UserDetails userDetails;

    // A valid 256-bit base64-encoded secret key for testing
    private final String testSecret = "Y29udGFjdE1hbmFnZW1lbnRTeXN0ZW1TZWNyZXRLZXkyMDI2UHJvZHVjdGlvbkp3dA==";
    private final long testExpiration = 86400000; // 24 hours

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", testSecret);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpiration", testExpiration);

        userDetails = new User("testuser@example.com", "password", Collections.emptyList());
    }

    @Test
    @DisplayName("generateToken - Creates a valid token with username")
    void generateToken_Success() {
        String token = jwtTokenProvider.generateToken(userDetails);

        assertThat(token).isNotBlank();
        assertThat(token.split("\\.")).hasSize(3); // Header, Payload, Signature
    }

    @Test
    @DisplayName("extractUsername - Retrieves correct username from valid token")
    void extractUsername_Success() {
        String token = jwtTokenProvider.generateToken(userDetails);
        String extractedUsername = jwtTokenProvider.extractUsername(token);

        assertThat(extractedUsername).isEqualTo(userDetails.getUsername());
    }

    @Test
    @DisplayName("isTokenValid - Returns true for valid token and matching user")
    void isTokenValid_ValidToken() {
        String token = jwtTokenProvider.generateToken(userDetails);
        boolean isValid = jwtTokenProvider.isTokenValid(token, userDetails);

        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("isTokenValid - Returns false for mismatching user")
    void isTokenValid_InvalidUser() {
        String token = jwtTokenProvider.generateToken(userDetails);
        UserDetails wrongUser = new User("wrong@example.com", "pass", Collections.emptyList());

        boolean isValid = jwtTokenProvider.isTokenValid(token, wrongUser);

        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("validateToken - Returns true for valid token")
    void validateToken_ValidToken() {
        String token = jwtTokenProvider.generateToken(userDetails);
        boolean isValid = jwtTokenProvider.validateToken(token);

        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("validateToken - Returns false for malformed token")
    void validateToken_MalformedToken() {
        boolean isValid = jwtTokenProvider.validateToken("this.is.not.a.real.token");
        assertThat(isValid).isFalse();
        
        boolean isValidEmpty = jwtTokenProvider.validateToken("");
        assertThat(isValidEmpty).isFalse();
    }

    @Test
    @DisplayName("validateToken - Returns false for expired token")
    void validateToken_ExpiredToken() {
        // Temporarily set expiration to a negative value to force immediate expiration
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpiration", -10000L);
        String expiredToken = jwtTokenProvider.generateToken(userDetails);

        boolean isValid = jwtTokenProvider.validateToken(expiredToken);

        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("validateToken - Returns false for token with invalid signature")
    void validateToken_InvalidSignature() {
        // Create token with a different secret
        String differentSecret = "VGhpc0lzQURpZmZlcmVudFNlY3JldEtleUZvclRlc3RpbmdQdXJwb3Nlc09ubHkxMjM=";
        byte[] keyBytes = Decoders.BASE64.decode(differentSecret);
        SecretKey fakeKey = Keys.hmacShaKeyFor(keyBytes);

        String fakeToken = Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + testExpiration))
                .signWith(fakeKey)
                .compact();

        boolean isValid = jwtTokenProvider.validateToken(fakeToken);

        assertThat(isValid).isFalse();
    }
}
