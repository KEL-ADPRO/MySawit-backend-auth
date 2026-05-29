package com.mysawit.mysawit_auth.service;

import com.mysawit.mysawit_auth.exception.InvalidCredentialException;
import com.mysawit.mysawit_auth.model.Role;
import com.mysawit.mysawit_auth.util.JwtUtil;
import com.mysawit.mysawit_auth.util.TokenBlacklist;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenAuthorizationServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private TokenBlacklist tokenBlacklist;

    @InjectMocks
    private TokenAuthorizationServiceImpl tokenAuthorizationService;

    private final String VALID_TOKEN = "valid.jwt.token";
    private final UUID EXPECTED_USER_ID = UUID.randomUUID();

    @Test
    void requireRoleSuccess() {
        when(tokenBlacklist.isBlacklisted(VALID_TOKEN)).thenReturn(false);
        when(jwtUtil.extractUserId(VALID_TOKEN)).thenReturn(EXPECTED_USER_ID.toString());
        when(jwtUtil.extractRole(VALID_TOKEN)).thenReturn(Role.ADMIN.name());

        UUID actualUserId = tokenAuthorizationService.requireRole(VALID_TOKEN, Role.ADMIN);

        assertEquals(EXPECTED_USER_ID, actualUserId);
        verify(tokenBlacklist).isBlacklisted(VALID_TOKEN);
        verify(jwtUtil).extractUserId(VALID_TOKEN);
        verify(jwtUtil).extractRole(VALID_TOKEN);
    }

    @Test
    void requireRoleNullToken() {
        assertThrows(InvalidCredentialException.class, () -> tokenAuthorizationService.requireRole(null, Role.ADMIN));

        verifyNoInteractions(tokenBlacklist, jwtUtil);
    }

    @Test
    void requireRoleBlankToken() {
        assertThrows(InvalidCredentialException.class, () -> tokenAuthorizationService.requireRole("", Role.ADMIN));

        verifyNoInteractions(tokenBlacklist, jwtUtil);
    }

    @Test
    void requireRoleBlacklistedToken() {
        when(tokenBlacklist.isBlacklisted(VALID_TOKEN)).thenReturn(true);

        assertThrows(InvalidCredentialException.class, () -> tokenAuthorizationService.requireRole(VALID_TOKEN, Role.ADMIN));

        verify(tokenBlacklist).isBlacklisted(VALID_TOKEN);
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void requireRoleJwtExtractionFails() {
        when(tokenBlacklist.isBlacklisted(VALID_TOKEN)).thenReturn(false);
        RuntimeException rootCause = new RuntimeException("JWT Expired");
        when(jwtUtil.extractUserId(VALID_TOKEN)).thenThrow(rootCause);

        InvalidCredentialException thrown = assertThrows(InvalidCredentialException.class, () -> tokenAuthorizationService.requireRole(VALID_TOKEN, Role.ADMIN));

        assertEquals(rootCause, thrown.getCause());
    }

    @Test
    void requireRoleRoleMismatch() {
        when(tokenBlacklist.isBlacklisted(VALID_TOKEN)).thenReturn(false);
        when(jwtUtil.extractUserId(VALID_TOKEN)).thenReturn(EXPECTED_USER_ID.toString());
        when(jwtUtil.extractRole(VALID_TOKEN)).thenReturn(Role.BURUH.name());

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> tokenAuthorizationService.requireRole(VALID_TOKEN, Role.ADMIN));

        assertEquals("Access denied: ADMIN role required", thrown.getMessage());
    }
}