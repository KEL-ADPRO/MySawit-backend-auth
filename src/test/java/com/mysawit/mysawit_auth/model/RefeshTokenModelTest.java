package com.mysawit.mysawit_auth.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class RefeshTokenModelTest {
    private RefreshToken refreshToken;
    private final UUID USER_ID = UUID.fromString("eb558e9f-1c39-460e-8860-71af6af63bd6");
    private final String TOKEN_VALUE = "refresh.token.value.long.string";
    private final Instant CREATED_AT = Instant.now();
    private final Instant EXPIRES_AT = Instant.now().plusSeconds(10);

    @BeforeEach
    void setUp() {
        refreshToken = RefreshToken.builder()
                .id(UUID.randomUUID())
                .userId(USER_ID)
                .token(TOKEN_VALUE)
                .expiresAt(EXPIRES_AT)
                .createdAt(CREATED_AT)
                .isRevoked(false)
                .build();
    }

    @Test
    void getToken() {
        final String token = refreshToken.getToken();
        assertEquals(TOKEN_VALUE, token);
    }

    @Test
    void getUserId() {
        final UUID userId = refreshToken.getUserId();
        assertEquals(USER_ID, userId);
    }

    @Test
    void getExpiresAt() {
        final Instant expireTime = refreshToken.getExpiresAt();
        assertEquals(EXPIRES_AT, expireTime);
    }

    @Test
    void getCreatedAt() {
        final Instant createdAt = refreshToken.getCreatedAt();
        assertEquals(CREATED_AT, createdAt);
    }

    @Test
    void getIsRevoked() {
        final boolean revoked = refreshToken.isRevoked();
        assertFalse(revoked);
    }

    @Test
    void tokenIsExpired() {
        final Instant pastExpiry = Instant.now().minusSeconds(3600);
        refreshToken.setExpiresAt(pastExpiry);
        assertTrue(refreshToken.getExpiresAt().isBefore(Instant.now()));
    }

    @Test
    void tokenIsNotExpired() {
        final Instant futureExpiry = Instant.now().plusSeconds(3600);
        refreshToken.setExpiresAt(futureExpiry);
        assertTrue(refreshToken.getExpiresAt().isAfter(Instant.now()));
    }

    @Test
    void tokenCanBeRevoked() {
        assertFalse(refreshToken.isRevoked());
        refreshToken.setRevoked(true);
        assertTrue(refreshToken.isRevoked());
    }

    @Test
    void createdAtIsSet() {
        final Instant before = Instant.now();
        final RefreshToken token = RefreshToken.builder()
                .userId(USER_ID)
                .token(TOKEN_VALUE)
                .expiresAt(EXPIRES_AT)
                .createdAt(Instant.now())
                .build();
        final Instant after = Instant.now();

        assertNotNull(token.getCreatedAt());
        assertFalse(token.getCreatedAt().isBefore(before));
        assertFalse(token.getCreatedAt().isAfter(after.plusSeconds(1)));
    }

    @Test
    void multipleTokensSameUserDifferentTokens() {
        final RefreshToken token1 = RefreshToken.builder()
                .id(UUID.randomUUID())
                .userId(USER_ID)
                .token("token.1")
                .expiresAt(Instant.now().plusSeconds(3600))
                .createdAt(Instant.now())
                .isRevoked(false)
                .build();

        final RefreshToken token2 = RefreshToken.builder()
                .id(UUID.randomUUID())
                .userId(USER_ID)
                .token("token.2")
                .expiresAt(Instant.now().plusSeconds(3600))
                .createdAt(Instant.now())
                .isRevoked(false)
                .build();

        assertNotEquals(token1.getId(), token2.getId());
        assertNotEquals(token1.getToken(), token2.getToken());
        assertEquals(token1.getUserId(), token2.getUserId());
    }
}
