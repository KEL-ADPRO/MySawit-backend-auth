package com.mysawit.mysawit_auth.handler;

import com.mysawit.mysawit_auth.exception.*;
import com.mysawit.mysawit_auth.dto.response.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;

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
    void handleEmailAlreadyExists_returns400() {
        final ResponseEntity<ApiResponse<Void>> response = handler.handleEmailAlreadyExists(new EmailAlreadyExistsException("admin@gmail.com"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Email admin@gmail.com is already registered", response.getBody().getMessage());
    }

    @Test
    void handleUsernameAlreadyExists_returns400() {
        final ResponseEntity<ApiResponse<Void>> response = handler.handleUsernameAlreadyExists(new UsernameAlreadyExistsException("admin"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Username admin is already registered", response.getBody().getMessage());
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
    void handleAccountLocked_returns429() {
        final ResponseEntity<ApiResponse<Void>> response = handler.handleAccountLocked(new AccountLockedException(Instant.MAX));

        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Account is temporarily locked due to too many failed login attempts. Try again later.", response.getBody().getMessage());
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

    @Test
    void handleSelfDeletion_returns403() {
        final ResponseEntity<ApiResponse<Void>> response = handler.handleSelfDeletion(new SelfDeletionException());

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Admin cannot delete self", response.getBody().getMessage());
    }
}