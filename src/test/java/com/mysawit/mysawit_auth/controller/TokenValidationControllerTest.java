package com.mysawit.mysawit_auth.controller;

import com.mysawit.mysawit_auth.handler.GlobalExceptionHandler;
import com.mysawit.mysawit_auth.model.Role;
import com.mysawit.mysawit_auth.util.JwtUtil;
import com.mysawit.mysawit_auth.util.TokenBlacklist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class TokenValidationControllerTest {
    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private TokenBlacklist tokenBlacklist;

    @InjectMocks
    private TokenValidationController tokenValidationController;

    private MockMvc mockMvc;
    private final String VALID_TOKEN = "valid.jwt.token";
    private final String USER_ID = "eb558e9f-1c39-460e-8860-71af6af63bd6";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(tokenValidationController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void validate_validToken_returnsValidTrueWithClaims() throws Exception {
        when(tokenBlacklist.isBlacklisted(VALID_TOKEN)).thenReturn(false);
        when(jwtUtil.extractUserId(VALID_TOKEN)).thenReturn(USER_ID);
        when(jwtUtil.extractRole(VALID_TOKEN)).thenReturn("ADMIN");

        mockMvc.perform(get("/api/auth/validate")
                        .header("Authorization", "Bearer " + VALID_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Token valid"))
                .andExpect(jsonPath("$.data.valid").value(true))
                .andExpect(jsonPath("$.data.userId").value(USER_ID))
                .andExpect(jsonPath("$.data.role").value("ADMIN"));
    }

    @Test
    void validate_missingAuthHeader_returnsValidFalse() throws Exception {
        mockMvc.perform(get("/api/auth/validate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.valid").value(false))
                .andExpect(jsonPath("$.data.userId").doesNotExist())
                .andExpect(jsonPath("$.data.role").doesNotExist());

        verifyNoInteractions(jwtUtil, tokenBlacklist);
    }

    @Test
    void validate_malformedAuthHeader_returnsValidFalse() throws Exception {
        mockMvc.perform(get("/api/auth/validate")
                        .header("Authorization", "NotBearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.valid").value(false));

        verifyNoInteractions(jwtUtil, tokenBlacklist);
    }

    @Test
    void validate_blacklistedToken_returnsValidFalse() throws Exception {
        when(tokenBlacklist.isBlacklisted(VALID_TOKEN)).thenReturn(true);

        mockMvc.perform(get("/api/auth/validate")
                        .header("Authorization", "Bearer " + VALID_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.valid").value(false));

        verify(jwtUtil, never()).extractUserId(any());
        verify(jwtUtil, never()).extractRole(any());
    }

    @Test
    void validate_expiredOrMalformedToken_returnsValidFalse() throws Exception {
        when(tokenBlacklist.isBlacklisted(VALID_TOKEN)).thenReturn(false);
        when(jwtUtil.extractUserId(VALID_TOKEN)).thenThrow(new RuntimeException("JWT expired"));

        mockMvc.perform(get("/api/auth/validate")
                        .header("Authorization", "Bearer " + VALID_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.valid").value(false));
    }

    @Test
    void validate_allRolesReturnedCorrectly() throws Exception {
        for (final Role role : Role.values()) {
            when(tokenBlacklist.isBlacklisted(VALID_TOKEN)).thenReturn(false);
            when(jwtUtil.extractUserId(VALID_TOKEN)).thenReturn(USER_ID);
            when(jwtUtil.extractRole(VALID_TOKEN)).thenReturn(role.name());

            mockMvc.perform(get("/api/auth/validate")
                            .header("Authorization", "Bearer " + VALID_TOKEN))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.valid").value(true))
                    .andExpect(jsonPath("$.data.role").value(role.name()));
        }
    }

    @Test
    void validate_validToken_doesNotExposeInternalErrors() throws Exception {
        when(tokenBlacklist.isBlacklisted(VALID_TOKEN)).thenReturn(false);
        when(jwtUtil.extractUserId(VALID_TOKEN)).thenThrow(new RuntimeException("internal error details"));

        mockMvc.perform(get("/api/auth/validate")
                        .header("Authorization", "Bearer " + VALID_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.valid").value(false))
                .andExpect(jsonPath("$.message").value("Token invalid"));
    }
}