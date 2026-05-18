package com.mysawit.mysawit_auth.controller;

import com.mysawit.mysawit_auth.service.AuthService;
import com.mysawit.mysawit_auth.dto.response.ApiResponse;
import com.mysawit.mysawit_auth.dto.response.AuthResponse;
import com.mysawit.mysawit_auth.dto.request.LoginRequest;
import com.mysawit.mysawit_auth.dto.request.RegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody final RegisterRequest request) {
        final AuthResponse response = authService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.successResponse("Registration successful", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody final LoginRequest request) {
        final AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.successResponse("Login successful", response));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthResponse>> getMe(@RequestHeader(value = "Authorization", required = false) final String authHeader) {
        final String token = extractBearer(authHeader);
        final AuthResponse response = authService.getLoggedInUser(token);
        return ResponseEntity.ok(ApiResponse.successResponse("User retrieved", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader(value = "Authorization", required = false) final String authHeader) {
        final String token = extractBearer(authHeader);
        authService.logout(token);
        return ResponseEntity.ok(ApiResponse.successResponse("Logout successful", null));
    }

    private String extractBearer(final String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid Authorization header");
        }
        return authHeader.substring(7);
    }
}