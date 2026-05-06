package com.mysawit.mysawit_auth.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TokenBlacklistTest {
    private TokenBlacklist tokenBlacklist;

    @BeforeEach
    void setUp() {
        tokenBlacklist = new TokenBlacklistImpl();
    }

    @Test
    void freshTokenIsNotBlacklisted() {
        assertFalse(tokenBlacklist.isBlacklisted("fresh.jwt.token"));
    }

    @Test
    void blacklistedTokenIsRecognised() {
        tokenBlacklist.blacklist("blacklisted.jwt.token");
        assertTrue(tokenBlacklist.isBlacklisted("blacklisted.jwt.token"));
    }

    @Test
    void blacklistingOneTokenDoesNotAffectAnother() {
        tokenBlacklist.blacklist("token.a");
        assertFalse(tokenBlacklist.isBlacklisted("token.b"));
    }
}