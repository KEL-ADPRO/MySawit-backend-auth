package com.mysawit.mysawit_auth.util;

import com.mysawit.mysawit_auth.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilTest {
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() throws Exception {
        jwtUtil = new JwtUtil();

        Field secretField = JwtUtil.class.getDeclaredField("jwtSecret");
        secretField.setAccessible(true);
        secretField.set(jwtUtil, "testSecretKeyABCDEFGHIJKLMNOPQRS");

        Field expirationField = JwtUtil.class.getDeclaredField("jwtExpirationMs");
        expirationField.setAccessible(true);
        expirationField.set(jwtUtil, 3600000L);
    }

    @Test
    void generateNewToken() {
        final String jwt = jwtUtil.generateToken(UUID.fromString("eb558e9f-1c39-460e-8860-71af6af63bd6"), Role.ADMIN);
        assertNotNull(jwt);
    }

    @Test
    void extractUserIdFromToken() {
        final String jwt = jwtUtil.generateToken(UUID.fromString("eb558e9f-1c39-460e-8860-71af6af63bd6"), Role.ADMIN);
        final String userId = jwtUtil.extractUserId(jwt);
        assertEquals("eb558e9f-1c39-460e-8860-71af6af63bd6", userId);
    }

    @Test
    void extractUserIdFromDifferentToken() {
        final String jwt = jwtUtil.generateToken(UUID.fromString("eb558e9f-1c39-460e-8860-71af6af63bd6"), Role.ADMIN);
        final String userId = jwtUtil.extractUserId(jwt);
        assertNotEquals("Eb558e9f-1c39-460e-8860-71af6af63bd6", userId);
    }

    @Test
    void checkValidToken() {
        final String jwt = jwtUtil.generateToken(UUID.fromString("eb558e9f-1c39-460e-8860-71af6af63bd6"), Role.ADMIN);
        assertTrue(jwtUtil.isTokenValid(jwt, UUID.fromString("eb558e9f-1c39-460e-8860-71af6af63bd6")));
    }

    @Test
    void checkInvalidToken() {
        final String jwt = jwtUtil.generateToken(UUID.fromString("eb558e9f-1c39-460e-8860-71af6af63bd6"), Role.ADMIN);
        assertFalse(jwtUtil.isTokenValid(jwt, UUID.fromString("Ab558e9f-1c39-460e-8860-71af6af63bd6")));
    }

    @Test
    void extracExpirationFromToken() {
        final String jwt = jwtUtil.generateToken(UUID.fromString("eb558e9f-1c39-460e-8860-71af6af63bd6"), Role.ADMIN);
        assertTrue(jwtUtil.extractExpiration(jwt).after(new Date()));
    }
}
