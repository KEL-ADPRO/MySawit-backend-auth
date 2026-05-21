package com.mysawit.mysawit_auth.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {
    public static final String AUTH_COOKIE_NAME = "auth_token";

    @Value("${jwt.expiration-ms}")
    private long jwtExpirationMs;

    @Value("${app.cookie.secure:true}")
    private boolean secureCookie;

    public String addAuthCookie(final String token) {
        ResponseCookie cookie = ResponseCookie.from(AUTH_COOKIE_NAME, token)
                .httpOnly(true)
                .secure(secureCookie)
                .path("/")
                .maxAge(jwtExpirationMs / 1000)
                .sameSite("Strict")
                .build();
        return cookie.toString();
    }

    public String clearAuthCookie() {
        ResponseCookie cookie = ResponseCookie.from(AUTH_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(secureCookie)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
        return cookie.toString();
    }
}