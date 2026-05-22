package com.mysawit.mysawit_auth.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {
    public static final String AUTH_COOKIE_NAME = "auth_token";
    public static final String REFRESH_COOKIE_NAME = "refresh_token";
    public static final String REFRESH_COOKIE_PATH = "/api/auth/refresh";

    @Value("${jwt.expiration-ms}")
    private long jwtExpirationMs;

    @Value("${app.refresh-token.expiration-days:7}")
    private long refreshExpirationDays;

    @Value("${app.cookie.secure:true}")
    private boolean secureCookie;

    public String addAuthCookie(final String token) {
        return buildCookie(AUTH_COOKIE_NAME, token, "/", jwtExpirationMs / 1000).toString();
    }

    public String addRefreshCookie(final String token) {
        return buildCookie(REFRESH_COOKIE_NAME, token, REFRESH_COOKIE_PATH, refreshExpirationDays * 24 * 60 * 60).toString();
    }

    public String clearAuthCookie() {
        return buildCookie(AUTH_COOKIE_NAME, "", "/", 0).toString();
    }

    public String clearRefreshCookie() {
        return buildCookie(REFRESH_COOKIE_NAME, "", REFRESH_COOKIE_PATH, 0).toString();
    }

    private ResponseCookie buildCookie(final String name, final String value, final String path, final long maxAgeSeconds) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(secureCookie)
                .path(path)
                .maxAge(maxAgeSeconds)
                .sameSite("Strict")
                .build();
    }
}