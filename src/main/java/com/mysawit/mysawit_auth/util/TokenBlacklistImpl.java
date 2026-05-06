package com.mysawit.mysawit_auth.util;

import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class TokenBlacklistImpl implements TokenBlacklist {
    private final ConcurrentMap<String, Long> blacklistedTokens = new ConcurrentHashMap<>();
    private final JwtUtil jwtUtil;

    public TokenBlacklistImpl(final JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void blacklist(final String token) {
        final long expiresAt = jwtUtil.extractExpiration(token).getTime();
        blacklistedTokens.put(token, expiresAt);
    }

    @Override
    public boolean isBlacklisted(final String token) {
        final Long expiresAt = blacklistedTokens.get(token);

        if (expiresAt == null) {
            return false;
        }

        if (System.currentTimeMillis() > expiresAt) {
            blacklistedTokens.remove(token);
            return false;
        }

        return true;
    }
}