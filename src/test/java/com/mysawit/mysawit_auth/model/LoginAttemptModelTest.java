package com.mysawit.mysawit_auth.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class LoginAttemptModelTest {
    private LoginAttempt loginAttempt;
    private final Instant FAIL_TIME = Instant.now();
    private final Instant LOCK_TIME = Instant.now().plusSeconds(10);

    @BeforeEach
    public void setUp() {
        loginAttempt = LoginAttempt.builder()
                .email("user@gmail.com")
                .failedCount(3)
                .lastFailedAt(FAIL_TIME)
                .lockedUntil(LOCK_TIME)
                .build();
    }

    @Test
    void getEmail() {
        final String email = loginAttempt.getEmail();
        assertEquals("user@gmail.com", email);
    }

    @Test
    void getFailedCount() {
        final int failedCount = loginAttempt.getFailedCount();
        assertEquals(3, failedCount);
    }

    @Test
    void getLastFailedAt() {
        final Instant lastFailedAt = loginAttempt.getLastFailedAt();

        assertEquals(FAIL_TIME, lastFailedAt);
    }

    @Test
    void getLockedUntil() {
        final Instant lockedUntil = loginAttempt.getLockedUntil();

        assertEquals(LOCK_TIME, lockedUntil);
    }
}
