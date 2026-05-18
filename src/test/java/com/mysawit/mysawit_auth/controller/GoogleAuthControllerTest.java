package com.mysawit.mysawit_auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysawit.mysawit_auth.dto.request.GoogleAuthRequest;
import com.mysawit.mysawit_auth.dto.response.AuthResponse;
import com.mysawit.mysawit_auth.handler.GlobalExceptionHandler;
import com.mysawit.mysawit_auth.model.Role;
import com.mysawit.mysawit_auth.service.GoogleAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class GoogleAuthControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private GoogleAuthRequest adminRequest;
    private AuthResponse adminResponse;

    @Mock
    private GoogleAuthService googleAuthService;

    @InjectMocks
    private GoogleAuthController googleAuthController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(googleAuthController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        adminRequest = GoogleAuthRequest.builder()
                .idToken("google.id.token")
                .username("Admin Sawit")
                .role(Role.ADMIN)
                .build();

        adminResponse = AuthResponse.builder()
                .token("jwt.token")
                .userId(UUID.fromString("eb558e9f-1c39-460e-8860-71af6af63bd6"))
                .username("Admin Sawit")
                .name("Agus")
                .email("admin@gmail.com")
                .role(Role.ADMIN)
                .build();
    }

    @Test
    void googleAuthAdminSuccess() throws Exception {
        when(googleAuthService.loginOrRegister(any(GoogleAuthRequest.class))).thenReturn(adminResponse);

        mockMvc.perform(post("/api/auth/google")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("Admin Sawit"))
                .andExpect(jsonPath("$.data.role").value("ADMIN"));

        verify(googleAuthService, times(1)).loginOrRegister(any(GoogleAuthRequest.class));
    }

    @Test
    void invalidGoogleToken() throws Exception {
        when(googleAuthService.loginOrRegister(any(GoogleAuthRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid Google ID token"));

        mockMvc.perform(post("/api/auth/google")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid Google ID token"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(googleAuthService, times(1)).loginOrRegister(any(GoogleAuthRequest.class));
    }
}
