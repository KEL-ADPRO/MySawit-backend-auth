package com.mysawit.mysawit_auth.handler;

import com.mysawit.mysawit_auth.exception.EmailAlreadyExistsException;
import com.mysawit.mysawit_auth.exception.InvalidCredentialException;
import com.mysawit.mysawit_auth.exception.MandorSertifMissingException;
import com.mysawit.mysawit_auth.exception.WeakPasswordException;
import com.mysawit.mysawit_auth.dto.response.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleInvalidCredential_returns401() {
        final ResponseEntity<ApiResponse<Void>> response = handler.handleInvalidCredential(new InvalidCredentialException());

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Invalid credentials", response.getBody().getMessage());
    }

    @Test
    void handleEmailAlreadyExists_returns409() {
        final ResponseEntity<ApiResponse<Void>> response = handler.handleEmailAlreadyExists(new EmailAlreadyExistsException("admin@gmail.com"));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Email admin@gmail.com is already registered", response.getBody().getMessage());
    }

    @Test
    void handleMandorSertifMissing_returns422() {
        final ResponseEntity<ApiResponse<Void>> response = handler.handleMandorSertifMissing(new MandorSertifMissingException());

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Nomor sertifikasi mandor is required for MANDOR role", response.getBody().getMessage());
    }

    @Test
    void handleWeakPassword_returns422() {
        final ResponseEntity<ApiResponse<Void>> response = handler.handleWeakPassword(new WeakPasswordException("Password must be at least 8 characters"));

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Password must be at least 8 characters", response.getBody().getMessage());
    }

    @Test
    void handleWeakPassword_preservesSpecificMessage() {
        final String[] messages = {
                "Password must be at least 8 characters",
                "Password must not exceed 72 characters",
                "Password must contain at least one uppercase letter",
                "Password must contain at least one lowercase letter",
                "Password must contain at least one digit",
                "Password must contain at least one special character"
        };

        for (final String message : messages) {
            final ResponseEntity<ApiResponse<Void>> response = handler.handleWeakPassword(new WeakPasswordException(message));
            assertNotNull(response.getBody());
            assertEquals(message, response.getBody().getMessage());
            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        }
    }

    @Test
    void handleIllegalArgument_returns400() {
        final ResponseEntity<ApiResponse<Void>> response = handler.handleIllegalArgument(new IllegalArgumentException("Invalid Authorization header"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Invalid Authorization header", response.getBody().getMessage());
    }

    @Test
    void handleGeneric_returns500() {
        final ResponseEntity<ApiResponse<Void>> response = handler.handleGeneric();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("An unexpected error occurred", response.getBody().getMessage());
    }
}