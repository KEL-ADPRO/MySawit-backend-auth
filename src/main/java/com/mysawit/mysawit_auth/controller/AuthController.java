package com.mysawit.mysawit_auth.controller;

import com.mysawit.mysawit_auth.dto.request.RefreshRequest;
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
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody final RegisterRequest request
    ) {
        final AuthResponse response = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.successResponse("Registration successful", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody final LoginRequest request
    ) {
        final AuthStrategy<LoginRequest> strategy = strategyFactory.resolve(AuthProvider.PASSWORD);
        final AuthResponse authResponse = strategy.authenticate(request);

        String accessCookie = cookieUtil.addAuthCookie(authResponse.getToken());
        String refreshCookie = cookieUtil.addRefreshCookie(authResponse.getRefreshToken());

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie)
                .header(HttpHeaders.SET_COOKIE, refreshCookie)
                .body(ApiResponse.successResponse("Login successful", authResponse));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthResponse>> getMe(
            @RequestHeader(value = "Authorization", required = false) final String authHeader,
            @CookieValue(value = CookieUtil.AUTH_COOKIE_NAME, required = false) final String cookieToken
    ) {
        final String token = resolveAccessToken(authHeader, cookieToken);
        final AuthResponse response = authService.getLoggedInUser(token);

        return ResponseEntity
                .ok()
                .body(ApiResponse.successResponse("User retrieved", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader(value = "Authorization", required = false) final String authHeader
    ) {
        final String token = extractBearer(authHeader);
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
            @CookieValue(value = CookieUtil.REFRESH_COOKIE_NAME, required = false) final String cookieRefreshToken,
            @RequestBody(required = false) final RefreshRequest body) {

        final String rawToken = resolveRefreshToken(cookieRefreshToken, body);
        final AuthResponse authResponse = authService.refresh(rawToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieUtil.addAuthCookie(authResponse.getToken()))
                .header(HttpHeaders.SET_COOKIE, cookieUtil.addRefreshCookie(authResponse.getRefreshToken()))
                .body(ApiResponse.successResponse("Token refreshed successfully", authResponse));
    }

    private String resolveAccessToken(final String authHeader, final String cookieToken) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        if (cookieToken != null && !cookieToken.isBlank()) {
            return cookieToken;
        }
        throw new IllegalArgumentException("Invalid Authorization header");
    }

    private String resolveRefreshToken(final String cookieToken, final RefreshRequest body) {
        if (cookieToken != null && !cookieToken.isBlank()) {
            return cookieToken;
        }
        if (body != null && body.getRefreshToken() != null && !body.getRefreshToken().isBlank()) {
            return body.getRefreshToken();
        }
        throw new IllegalArgumentException("Refresh token is missing");
    }

    private String extractBearer(final String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid Authorization header");
        }
        return authHeader.substring(7);
    }
}