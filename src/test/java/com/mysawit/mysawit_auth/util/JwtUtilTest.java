package com.mysawit.mysawit_auth.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

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
    public void testGenerateToken() {
        final String jwt = jwtUtil.generateToken("email@gmail.com");
        assertNotNull(jwt);
    }

    @Test
    public void testExtractEmail() {
        final String jwt = jwtUtil.generateToken("email@gmail.com");
        final String email = jwtUtil.extractEmail(jwt);
        assertEquals("email@gmail.com", email);
    }

    @Test
    public void testExtractEmailInvalid() {
        final String jwt = jwtUtil.generateToken("email@gmail.com");
        final String email = jwtUtil.extractEmail(jwt);
        assertNotEquals("gmail@gmail.com", email);
    }

    @Test
    public void testValidateToken() {
        final String jwt = jwtUtil.generateToken("email@gmail.com");
        assertTrue(jwtUtil.isTokenValid(jwt, "email@gmail.com"));
    }

    @Test
    public void testValidateTokenInvalid() {
        final String jwt = jwtUtil.generateToken("email@gmail.com");
        assertFalse(jwtUtil.isTokenValid(jwt, "gmail@gmail.com"));
    }
}
