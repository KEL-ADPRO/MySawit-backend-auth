package com.mysawit.mysawit_auth.controller;

import com.mysawit.mysawit_auth.dto.request.GoogleAuthRequest;
import com.mysawit.mysawit_auth.dto.response.ApiResponse;
import com.mysawit.mysawit_auth.dto.response.AuthResponse;
import com.mysawit.mysawit_auth.model.AuthProvider;
import com.mysawit.mysawit_auth.service.GoogleAuthService;
import com.mysawit.mysawit_auth.service.strategy.AuthStrategy;
import com.mysawit.mysawit_auth.service.strategy.AuthStrategyFactory;
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
    private final AuthStrategyFactory strategyFactory;

    @PostMapping
    public ResponseEntity<ApiResponse<AuthResponse>> loginOrRegister(@Valid @RequestBody final GoogleAuthRequest request) {
        final AuthStrategy<GoogleAuthRequest> strategy = strategyFactory.resolve(AuthProvider.GOOGLE);
        final AuthResponse response = strategy.authenticate(request);
        return ResponseEntity.ok(ApiResponse.successResponse("Google authentication successful", response));
    }
}
