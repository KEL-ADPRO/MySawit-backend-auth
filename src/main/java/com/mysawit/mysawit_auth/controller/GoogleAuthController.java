package com.mysawit.mysawit_auth.controller;

import com.mysawit.mysawit_auth.dto.request.AuthRequest;
import com.mysawit.mysawit_auth.dto.response.ApiResponse;
import com.mysawit.mysawit_auth.dto.response.AuthResponse;
import com.mysawit.mysawit_auth.service.GoogleAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/google")
@RequiredArgsConstructor
public class GoogleAuthController {
    private final GoogleAuthService googleAuthService;

    @PostMapping
    public ResponseEntity<ApiResponse<AuthResponse>> loginOrRegister(
            @Valid @RequestBody final AuthRequest request) {
        final AuthResponse response = googleAuthService.loginOrRegister(request);
        return ResponseEntity.ok(ApiResponse.successResponse("Google authentication successful", response));
    }
}
