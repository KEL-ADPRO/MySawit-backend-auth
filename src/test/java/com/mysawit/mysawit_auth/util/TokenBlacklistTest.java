package com.mysawit.mysawit_auth.util;

import com.mysawit.mysawit_auth.model.BlacklistedToken;
import com.mysawit.mysawit_auth.repository.BlacklistedTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TokenBlacklistTest {
    @Mock
    private BlacklistedTokenRepository tokenRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private TokenBlacklistImpl tokenBlacklist;

    @Test
    void nullTokenIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> tokenBlacklist.blacklist(null));
        verify(tokenRepository, never()).save(any());
    }

    @Test
    void freshTokenIsNotBlacklisted() {
        final String token = "fresh.jwt.token";
        when(tokenRepository.isTokenBlacklisted(eq(token), any(Instant.class))).thenReturn(false);

        assertFalse(tokenBlacklist.isBlacklisted(token));
    }

    @Test
    void expiredTokenIsBlacklisted() {
        final String token = "expired.jwt.token";
        when(tokenRepository.isTokenBlacklisted(eq(token), any(Instant.class))).thenReturn(true);

        assertTrue(tokenBlacklist.isBlacklisted(token));
    }

    @Test
    void blacklistSavesToRepository() {
        final String token = "valid.jwt.token";
        final Date expiry = new Date(System.currentTimeMillis() + 10_000);
        when(jwtUtil.extractExpiration(token)).thenReturn(expiry);

        tokenBlacklist.blacklist(token);

        verify(tokenRepository, times(1)).save(any(BlacklistedToken.class));
    }

    @Test
    void blacklistingOneTokenDoesNotAffectAnother() {
        final String tokenA = "jwt.token.a";
        final String tokenB = "jwt.token.b";
        final Date expiry = new Date(System.currentTimeMillis() + 10_000);
        when(jwtUtil.extractExpiration(tokenA)).thenReturn(expiry);
        when(tokenRepository.isTokenBlacklisted(eq(tokenB), any(Instant.class))).thenReturn(false);

        tokenBlacklist.blacklist(tokenA);

        assertFalse(tokenBlacklist.isBlacklisted(tokenB));
    }
}