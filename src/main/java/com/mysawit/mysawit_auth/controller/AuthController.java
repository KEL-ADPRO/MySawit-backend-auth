package com.mysawit.mysawit_auth.controller;

import com.mysawit.mysawit_auth.dto.request.AuthRequest;
import com.mysawit.mysawit_auth.dto.request.RefreshRequest;
import com.mysawit.mysawit_auth.service.AuthService;
import com.mysawit.mysawit_auth.dto.response.ApiResponse;
import com.mysawit.mysawit_auth.dto.response.AuthResponse;
import com.mysawit.mysawit_auth.dto.request.RegisterRequest;

import com.mysawit.mysawit_auth.util.BearerTokenExtractor;
import com.mysawit.mysawit_auth.util.CookieUtil;
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
    private final CookieUtil cookieUtil;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody final RegisterRequest request) {

        final AuthResponse response = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.successResponse("Registration successful", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody final AuthRequest request) {

        final AuthResponse response = authService.login(request);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieUtil.addAuthCookie(response.getToken()))
                .header(HttpHeaders.SET_COOKIE, cookieUtil.addRefreshCookie(response.getRefreshToken()))
                .body(ApiResponse.successResponse("Login successful", response));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthResponse>> getMe(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) final String authHeader,
            @CookieValue(value = CookieUtil.AUTH_COOKIE_NAME, required = false) final String cookieToken) {

        final String token = BearerTokenExtractor.resolve(authHeader, cookieToken);
        final AuthResponse response = authService.getLoggedInUser(token);

        return ResponseEntity
                .ok()
                .body(ApiResponse.successResponse("User retrieved", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) final String authHeader) {

        final String token = BearerTokenExtractor.extract(authHeader);
        authService.logout(token);

        String clearAccessCookie = cookieUtil.clearAuthCookie();
        String clearRefreshCookie = cookieUtil.clearRefreshCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clearAccessCookie)
                .header(HttpHeaders.SET_COOKIE, clearRefreshCookie)
                .body(ApiResponse.successResponse("Logout successful", null));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) final String authHeader,
            @RequestBody(required = false) final RefreshRequest body) {

        final String token = BearerTokenExtractor.resolve(authHeader, body.getRefreshToken());
        final AuthResponse authResponse = authService.refresh(token);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieUtil.addAuthCookie(authResponse.getToken()))
                .header(HttpHeaders.SET_COOKIE, cookieUtil.addRefreshCookie(authResponse.getRefreshToken()))
                .body(ApiResponse.successResponse("Token refreshed successfully", authResponse));
    }
}