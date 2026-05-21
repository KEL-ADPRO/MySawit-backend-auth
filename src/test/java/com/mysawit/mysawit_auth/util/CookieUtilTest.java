package com.mysawit.mysawit_auth.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class CookieUtilTest {
    private CookieUtil cookieUtil;

    @BeforeEach
    void setUp() throws Exception {
        cookieUtil = new CookieUtil();

        Field expirationField = CookieUtil.class.getDeclaredField("jwtExpirationMs");
        expirationField.setAccessible(true);
        expirationField.set(cookieUtil, 3600000L);

        Field secureField = CookieUtil.class.getDeclaredField("secureCookie");
        secureField.setAccessible(true);
        secureField.set(cookieUtil, true);
    }

    @Test
    void createAuthCookieHeaderSuccess() {
        final String sampleToken = "sample.jwt.token-string";

        final String cookieHeader = cookieUtil.addAuthCookie(sampleToken);

        assertNotNull(cookieHeader);
        assertTrue(cookieHeader.contains(CookieUtil.AUTH_COOKIE_NAME + "=" + sampleToken));
        assertTrue(cookieHeader.contains("Path=/"));
        assertTrue(cookieHeader.contains("HttpOnly"));
        assertTrue(cookieHeader.contains("Secure"));
        assertTrue(cookieHeader.contains("SameSite=Strict"));
        assertTrue(cookieHeader.contains("Max-Age=3600"));
    }

    @Test
    void createClearCookieHeaderSuccess() {
        final String clearCookieHeader = cookieUtil.clearAuthCookie();

        assertNotNull(clearCookieHeader);
        assertTrue(clearCookieHeader.contains(CookieUtil.AUTH_COOKIE_NAME + "="));
        assertTrue(clearCookieHeader.contains("Path=/"));
        assertTrue(clearCookieHeader.contains("HttpOnly"));
        assertTrue(clearCookieHeader.contains("Secure"));
        assertTrue(clearCookieHeader.contains("SameSite=Strict"));
        assertTrue(clearCookieHeader.contains("Max-Age=0"));
    }
}