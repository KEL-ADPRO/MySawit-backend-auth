package com.mysawit.mysawit_auth.util;

import com.mysawit.mysawit_auth.model.BlacklistedToken;
import com.mysawit.mysawit_auth.repository.BlacklistedTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@RequiredArgsConstructor
public class TokenBlacklistImpl implements TokenBlacklist {
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final JwtUtil jwtUtil;

    @Override
    public void blacklist(final String token) {
        if (token == null) {
            throw  new IllegalArgumentException("Token is null!");
        }

        final Instant expiresAt = jwtUtil.extractExpiration(token).toInstant();

        final BlacklistedToken blacklistedToken = BlacklistedToken.builder()
                .token(token)
                .expiresAt(expiresAt)
                .build();

        blacklistedTokenRepository.save(blacklistedToken);
    }

    @Override
    public boolean isBlacklisted(final String token) {
        return blacklistedTokenRepository.isTokenBlacklisted(token, Instant.now());
    }
}