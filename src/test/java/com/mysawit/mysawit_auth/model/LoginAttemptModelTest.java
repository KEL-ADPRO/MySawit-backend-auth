package com.mysawit.mysawit_auth.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class LoginAttemptModelTest {
    private LoginAttempt loginAttempt;

    @BeforeEach
    public void setUp() {
        loginAttempt = LoginAttempt.builder()
                .email("user@gmail.com")
                .failedCount(3)
                .lastFailedAt(Instant.now())
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
        final Instant failTime = Instant.now();
        loginAttempt.setLastFailedAt(failTime);
        final Instant lastFailedAt = loginAttempt.getLastFailedAt();

        assertEquals(failTime, lastFailedAt);
    }

    @Test
    void getLockedUntil() {
        final Instant lockTime = Instant.now().plusSeconds(10);
        loginAttempt.setLockedUntil(lockTime);
        final Instant lockedUntil = loginAttempt.getLockedUntil();

        assertEquals(lockTime, lockedUntil);
    }
}
