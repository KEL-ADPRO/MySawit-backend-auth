package com.mysawit.mysawit_auth.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TokenBlacklistTest {
    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private TokenBlacklistImpl tokenBlacklist;

    @Test
    void freshTokenIsNotBlacklisted() {
        assertFalse(tokenBlacklist.isBlacklisted("fresh.jwt.token"));
    }

    @Test
    void blacklistedTokenIsRecognised() {
        when(jwtUtil.extractExpiration("blacklisted.jwt.token")).thenReturn(new Date(System.currentTimeMillis() + 100));
        tokenBlacklist.blacklist("blacklisted.jwt.token");
        assertTrue(tokenBlacklist.isBlacklisted("blacklisted.jwt.token"));
    }

    @Test
    void blacklistingOneTokenDoesNotAffectAnother() {
        when(jwtUtil.extractExpiration("jwt.token.a")).thenReturn(new Date(System.currentTimeMillis() + 1000));
        tokenBlacklist.blacklist("jwt.token.a");
        assertFalse(tokenBlacklist.isBlacklisted("jwt.token.b"));
    }

    @Test
    void expiredBlacklistedTokenIsNotReported() {
        when(jwtUtil.extractExpiration("expired.jwt.token")).thenReturn(new Date(System.currentTimeMillis() - 100));
        tokenBlacklist.blacklist("expired.jwt.token");
        assertFalse(tokenBlacklist.isBlacklisted("expired.jwt.token"));
    }
}