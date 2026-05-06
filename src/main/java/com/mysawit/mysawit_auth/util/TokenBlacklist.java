package com.mysawit.mysawit_auth.util;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TokenBlacklist {
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    public void blacklist(final String token) {
    }

    public boolean isBlacklisted(final String token) {
        return true;
    }
}