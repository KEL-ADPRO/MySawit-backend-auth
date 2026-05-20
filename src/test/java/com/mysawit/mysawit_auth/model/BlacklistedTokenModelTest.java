package com.mysawit.mysawit_auth.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class BlacklistedTokenModelTest {
    private BlacklistedToken token;
    private final String TOKEN_VALUE = "eb558e9f-1c39-460e-8860-71af6af63bd6";
    private final Instant EXPIRES_AT = Instant.parse("2025-12-31T23:59:59Z");

    @BeforeEach
    void setUp() {
        token = BlacklistedToken.builder()
                .token(TOKEN_VALUE)
                .expiresAt(EXPIRES_AT)
                .build();
    }

    @Test
    void getToken() {
        assertEquals(TOKEN_VALUE, token.getToken());
    }

    @Test
    void getExpiresAt() {
        assertEquals(EXPIRES_AT, token.getExpiresAt());
    }

    @Test
    void setToken() {
        token.setToken("new.jwt.token");
        assertEquals("new.jwt.token", token.getToken());
    }

    @Test
    void setExpiresAt() {
        final Instant newExpiry = Instant.parse("2026-06-01T00:00:00Z");
        token.setExpiresAt(newExpiry);
        assertEquals(newExpiry, token.getExpiresAt());
    }

    @Test
    void builderProducesCorrectToken() {
        assertEquals(TOKEN_VALUE, token.getToken());
        assertEquals(EXPIRES_AT, token.getExpiresAt());
    }

    @Test
    void expiresAtInThePast() {
        final Instant pastExpiry = Instant.now().minusSeconds(3600);
        token.setExpiresAt(pastExpiry);
        assertTrue(token.getExpiresAt().isBefore(Instant.now()));
    }

    @Test
    void expiresAtInTheFuture() {
        final Instant futureExpiry = Instant.now().plusSeconds(3600);
        token.setExpiresAt(futureExpiry);
        assertTrue(token.getExpiresAt().isAfter(Instant.now()));
    }
}