package com.mysawit.mysawit_auth.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordHasherTest {

    private PasswordHasher passwordHasher;

    @BeforeEach
    void setUp() {
        passwordHasher = new PasswordHasher();
    }

    @Test
    void hashPassword() {
        final String rawPassword = "mySecretPassword123";
        final String hashedPassword = passwordHasher.hash(rawPassword);

        assertNotNull(hashedPassword);
        assertNotEquals(rawPassword, hashedPassword);
        assertTrue(hashedPassword.startsWith("$2a$") || hashedPassword.startsWith("$2b$") || hashedPassword.startsWith("$2y$"), "Should be a BCrypt hash");
    }

    @Test
    void matchesSuccess() {
        final String rawPassword = "mySecretPassword123";
        final String hashedPassword = passwordHasher.hash(rawPassword);

        assertTrue(passwordHasher.matches(rawPassword, hashedPassword));
    }

    @Test
    void matchesFailure() {
        final String rawPassword = "mySecretPassword123";
        final String wrongPassword = "wrongPassword";
        final String hashedPassword = passwordHasher.hash(rawPassword);

        assertFalse(passwordHasher.matches(wrongPassword, hashedPassword));
    }
}
