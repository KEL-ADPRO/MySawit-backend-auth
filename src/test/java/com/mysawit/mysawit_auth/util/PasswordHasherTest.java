package com.mysawit.mysawit_auth.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PasswordHasherTest {
    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    private PasswordHasher passwordHasher;

    final String rawPassword = "mySecretPassword123";

    @BeforeEach
    void setUp() {
        passwordHasher = new PasswordHasher(passwordEncoder);
    }

    @Test
    void hashPassword() {
        when(passwordEncoder.encode(rawPassword)).thenReturn("$2a$10$7EqJtq98hPqEX7fNZaFWoO5e8IYQyqvW5r0j/6cY5aY3u4i1K");
        final String hashedPassword = passwordHasher.hash(rawPassword);

        assertNotNull(hashedPassword);
        assertNotEquals(rawPassword, hashedPassword);
        assertTrue(hashedPassword.startsWith("$2a$") || hashedPassword.startsWith("$2b$") || hashedPassword.startsWith("$2y$"), "Should be a BCrypt hash");
    }

    @Test
    void matchesSuccess() {
        when(passwordEncoder.encode(rawPassword)).thenReturn("$2a$10$7EqJtq98hPqEX7fNZaFWoO5e8IYQyqvW5r0j/6cY5aY3u4i1K");
        when(passwordEncoder.matches(rawPassword, "$2a$10$7EqJtq98hPqEX7fNZaFWoO5e8IYQyqvW5r0j/6cY5aY3u4i1K")).thenReturn(true);

        final String hashedPassword = passwordHasher.hash(rawPassword);

        assertTrue(passwordHasher.matches(rawPassword, hashedPassword));
    }

    @Test
    void matchesFailure() {
        final String wrongPassword = "wrongPassword";
        when(passwordEncoder.encode(rawPassword)).thenReturn("$2a$10$7EqJtq98hPqEX7fNZaFWoO5e8IYQyqvW5r0j/6cY5aY3u4i1K");
        final String hashedPassword = passwordHasher.hash(rawPassword);
        when(passwordEncoder.matches(wrongPassword, hashedPassword)).thenReturn(false);

        assertFalse(passwordHasher.matches(wrongPassword, hashedPassword));
    }
}
