package com.mysawit.mysawit_auth.controller;

import com.mysawit.mysawit_auth.dto.request.GoogleAuthRequest;
import com.mysawit.mysawit_auth.dto.response.ApiResponse;
import com.mysawit.mysawit_auth.dto.response.AuthResponse;
import com.mysawit.mysawit_auth.model.Role;
import com.mysawit.mysawit_auth.service.GoogleAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GoogleAuthControllerTest {
    @Mock
    private GoogleAuthService googleAuthService;

    @InjectMocks
    private GoogleAuthController googleAuthController;

    private GoogleAuthRequest adminRequest;
    private AuthResponse adminResponse;

    @BeforeEach
    void setUp() {
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
    void googleAuthAdminSuccess() {
        when(googleAuthService.loginOrRegister(adminRequest)).thenReturn(adminResponse);

        final ResponseEntity<ApiResponse<AuthResponse>> result = googleAuthController.loginOrRegister(adminRequest);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().isSuccess());
        verify(googleAuthService, times(1)).loginOrRegister(adminRequest);

        final AuthResponse data = result.getBody().getData();
        assertEquals("Admin Sawit", data.getUsername());
        assertEquals(Role.ADMIN, data.getRole());
    }

    @Test
    void invalidGoogleToken() {
        when(googleAuthService.loginOrRegister(adminRequest)).thenThrow(new IllegalArgumentException("Invalid Google ID token"));

        final ResponseEntity<ApiResponse<AuthResponse>> result = googleAuthController.loginOrRegister(adminRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        assertFalse(result.getBody().isSuccess());
        assertEquals("Invalid Google ID token", result.getBody().getMessage());
        assertNull(result.getBody().getData());
    }
}
