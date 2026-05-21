package com.mysawit.mysawit_auth.controller;

import com.mysawit.mysawit_auth.model.AuthProvider;
import com.mysawit.mysawit_auth.service.AuthService;
import com.mysawit.mysawit_auth.dto.response.ApiResponse;
import com.mysawit.mysawit_auth.dto.response.AuthResponse;
import com.mysawit.mysawit_auth.dto.request.LoginRequest;
import com.mysawit.mysawit_auth.dto.request.RegisterRequest;
import com.mysawit.mysawit_auth.service.strategy.AuthStrategy;
import com.mysawit.mysawit_auth.service.strategy.AuthStrategyFactory;
import com.mysawit.mysawit_auth.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final AuthStrategyFactory strategyFactory;
    private final CookieUtil cookieUtil;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody final RegisterRequest request) {
        final AuthResponse response = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.successResponse("Registration successful", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody final LoginRequest request) {
        final AuthStrategy<LoginRequest> strategy = strategyFactory.resolve(AuthProvider.PASSWORD);
        final AuthResponse authResponse = strategy.authenticate(request);

        String cookie = cookieUtil.addAuthCookie(authResponse.getToken());

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, cookie)
                .body(ApiResponse.successResponse("Login successful", authResponse));
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

        String clearCookie = cookieUtil.clearAuthCookie();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, clearCookie)
                .body(ApiResponse.successResponse("Logout successful", null));
    }

    private String extractBearer(final String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid Authorization header");
        }

        return authHeader.substring(7);
    }
}