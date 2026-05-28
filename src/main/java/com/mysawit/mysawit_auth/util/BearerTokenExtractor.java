package com.mysawit.mysawit_auth.util;

public final class BearerTokenExtractor {
    private static final String BEARER_PREFIX = "Bearer ";

    public static String extract(final String authHeader) {
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            throw new IllegalArgumentException("Invalid Authorization header");
        }
        return authHeader.substring(BEARER_PREFIX.length());
    }
}