package com.mysawit.mysawit_auth.util;

import org.springframework.stereotype.Component;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenBlacklistImpl implements TokenBlacklist {
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    @Override
    public void blacklist(final String token) {
        blacklistedTokens.add(token);
    }

    @Override
    public boolean isBlacklisted(final String token) {
        return blacklistedTokens.contains(token);
    }
}