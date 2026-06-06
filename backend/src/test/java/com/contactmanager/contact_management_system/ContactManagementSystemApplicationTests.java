package com.contactmanager.contact_management_system;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Smoke test that verifies the Spring application context loads successfully
 * using the H2 in-memory database (test profile).
 */
@SpringBootTest(classes = ContactManagementSystemApplication.class)
@ActiveProfiles("test")
class ContactManagementSystemApplicationTests {

	@Test
	void contextLoads() {
		// Verifies that the full application context starts without errors.
	}

}
