package com.mysawit.mysawit_auth.controller;

import com.mysawit.mysawit_auth.exception.EmailAlreadyExistsException;
import com.mysawit.mysawit_auth.exception.MandorSertifMissingException;
import com.mysawit.mysawit_auth.model.Role;
import com.mysawit.mysawit_auth.service.AuthService;
import com.mysawit.mysawit_auth.util.ApiResponse;
import com.mysawit.mysawit_auth.util.AuthResponse;
import com.mysawit.mysawit_auth.util.RegisterRequest;
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
public class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private RegisterRequest adminRequest;
    private RegisterRequest mandorRequest;
    private RegisterRequest buruhRequest;
    private RegisterRequest supirRequest;
    private AuthResponse adminResponse;
    private AuthResponse mandorResponse;
    private AuthResponse buruhResponse;
    private AuthResponse supirResponse;

    @BeforeEach
    void setUp() {
        adminRequest = RegisterRequest.builder()
                .username("Admin Sawit")
                .name("Agus")
                .email("admin@gmail.com")
                .password("admin123")
                .role(Role.ADMIN)
                .build();

        mandorRequest = RegisterRequest.builder()
                .username("Mandor Sawit")
                .name("Burhan")
                .email("burhan@gmail.com")
                .password("mandor123")
                .role(Role.MANDOR)
                .nomorSertifMandor("CERT-001")
                .build();

        buruhRequest = RegisterRequest.builder()
                .username("Buruh Sawit")
                .name("Usep")
                .email("usep@gmail.com")
                .password("buruh123")
                .role(Role.BURUH)
                .build();

        supirRequest = RegisterRequest.builder()
                .username("Supir Sawit")
                .name("Budi")
                .email("budi@gmail.com")
                .password("supir123")
                .role(Role.SUPIR)
                .build();

        adminResponse = AuthResponse.builder()
                .userId(UUID.fromString("eb558e9f-1c39-460e-8860-71af6af63bd6"))
                .username("Admin Sawit")
                .name("Agus")
                .email("admin@gmail.com")
                .role(Role.ADMIN)
                .build();

        mandorResponse = AuthResponse.builder()
                .userId(UUID.fromString("fc558e9f-1c39-460e-8860-71af6af63bd6"))
                .username("Mandor Sawit")
                .name("Burhan")
                .email("burhan@gmail.com")
                .role(Role.MANDOR)
                .nomorSertifMandor("CERT-001")
                .build();

        buruhResponse = AuthResponse.builder()
                .userId(UUID.fromString("136d7ac3-2ee7-4e74-8225-e9062540a6c8"))
                .username("Buruh Sawit")
                .name("Usep")
                .email("usep@gmail.com")
                .role(Role.BURUH)
                .build();

        supirResponse = AuthResponse.builder()
                .userId(UUID.fromString("ec1d34bc-49a2-46d1-8cf0-ac8e559d508a"))
                .username("Supir Sawit")
                .name("Budi")
                .email("budi@gmail.com")
                .role(Role.SUPIR)
                .build();
    }

    @Test
    void registerAdmin_Success() {
        when(authService.register(adminRequest)).thenReturn(adminResponse);

        ResponseEntity<ApiResponse<AuthResponse>> result = authController.register(adminRequest);

        assertNotNull(result.getBody());
        assertEquals("Registration successful", result.getBody().getMessage());
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        verify(authService, times(1)).register(adminRequest);
    }

    @Test
    void registerAdmin_CorrectData() {
        when(authService.register(adminRequest)).thenReturn(adminResponse);

        ResponseEntity<ApiResponse<AuthResponse>> result = authController.register(adminRequest);

        assertNotNull(result.getBody());
        AuthResponse data = result.getBody().getData();

        assertEquals("Admin Sawit", data.getUsername());
        assertEquals("Agus", data.getName());
        assertEquals("admin@gmail.com", data.getEmail());
        assertEquals(Role.ADMIN, data.getRole());
        verify(authService, times(1)).register(adminRequest);
    }

    @Test
    void registerMandor_Success() {
        when(authService.register(mandorRequest)).thenReturn(mandorResponse);

        ResponseEntity<ApiResponse<AuthResponse>> result = authController.register(mandorRequest);

        assertNotNull(result.getBody());
        assertEquals("Registration successful", result.getBody().getMessage());
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        verify(authService, times(1)).register(mandorRequest);
    }

    @Test
    void registerMandor_CorrectData() {
        when(authService.register(mandorRequest)).thenReturn(mandorResponse);

        ResponseEntity<ApiResponse<AuthResponse>> result = authController.register(mandorRequest);

        assertNotNull(result.getBody());
        AuthResponse data = result.getBody().getData();

        assertEquals("Mandor Sawit", data.getUsername());
        assertEquals("Burhan", data.getName());
        assertEquals("burhan@gmail.com", data.getEmail());
        assertEquals(Role.MANDOR, data.getRole());
        assertEquals("CERT-001", data.getNomorSertifMandor());
        verify(authService, times(1)).register(mandorRequest);
    }

    @Test
    void registerBuruh_Success() {
        when(authService.register(buruhRequest)).thenReturn(buruhResponse);

        ResponseEntity<ApiResponse<AuthResponse>> result = authController.register(buruhRequest);

        assertNotNull(result.getBody());
        assertEquals("Registration successful", result.getBody().getMessage());
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        verify(authService, times(1)).register(buruhRequest);
    }

    @Test
    void registerBuruh_CorrectData() {
        when(authService.register(buruhRequest)).thenReturn(buruhResponse);

        ResponseEntity<ApiResponse<AuthResponse>> result = authController.register(buruhRequest);

        assertNotNull(result.getBody());
        AuthResponse data = result.getBody().getData();

        assertEquals("Buruh Sawit", data.getUsername());
        assertEquals("Usep", data.getName());
        assertEquals("usep@gmail.com", data.getEmail());
        assertEquals(Role.BURUH, data.getRole());
        verify(authService, times(1)).register(buruhRequest);
    }

    @Test
    void registerSupir_Success() {
        when(authService.register(supirRequest)).thenReturn(supirResponse);

        ResponseEntity<ApiResponse<AuthResponse>> result = authController.register(supirRequest);

        assertNotNull(result.getBody());
        assertEquals("Registration successful", result.getBody().getMessage());
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        verify(authService, times(1)).register(supirRequest);
    }

    @Test
    void registerSupir_CorrectData() {
        when(authService.register(supirRequest)).thenReturn(supirResponse);

        ResponseEntity<ApiResponse<AuthResponse>> result = authController.register(supirRequest);

        assertNotNull(result.getBody());
        AuthResponse data = result.getBody().getData();

        assertEquals("Supir Sawit", data.getUsername());
        assertEquals("Budi", data.getName());
        assertEquals("budi@gmail.com", data.getEmail());
        assertEquals(Role.SUPIR, data.getRole());
        verify(authService, times(1)).register(supirRequest);
    }
}