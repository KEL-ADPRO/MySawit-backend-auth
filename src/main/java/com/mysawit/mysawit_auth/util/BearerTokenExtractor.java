package com.mysawit.mysawit_auth.util;

public final class BearerTokenExtractor {
    private static final String BEARER_PREFIX = "Bearer ";

    private BearerTokenExtractor() {}

    public static String extract(final String authHeader) {
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            throw new IllegalArgumentException("Invalid Authorization header");
        }
        return authHeader.substring(BEARER_PREFIX.length());
    }

    public static String resolve(final String authHeader, final String cookieToken) {
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length());
        }
        if (cookieToken != null && !cookieToken.isBlank()) {
            return cookieToken;
        }
        throw new IllegalArgumentException("Invalid Authorization header");
    }
}