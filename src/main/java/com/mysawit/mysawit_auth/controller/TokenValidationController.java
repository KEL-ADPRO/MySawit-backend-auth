package com.mysawit.mysawit_auth.controller;

import com.mysawit.mysawit_auth.dto.response.ApiResponse;
import com.mysawit.mysawit_auth.dto.response.TokenValidationResponse;
import com.mysawit.mysawit_auth.model.Role;
import com.mysawit.mysawit_auth.util.JwtUtil;
import com.mysawit.mysawit_auth.util.TokenBlacklist;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class TokenValidationController {

    private final JwtUtil jwtUtil;
    private final TokenBlacklist tokenBlacklist;

    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<TokenValidationResponse>> validate(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) final String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.ok(ApiResponse.successResponse("Token invalid",
                    TokenValidationResponse.builder().valid(false).build()));
        }

        final String token = authHeader.substring(7);

        try {
            if (tokenBlacklist.isBlacklisted(token)) {
                return ResponseEntity.ok(ApiResponse.successResponse("Token invalid",
                        TokenValidationResponse.builder().valid(false).build()));
            }

            final String userId = jwtUtil.extractUserId(token);
            final String role = jwtUtil.extractRole(token);

            final TokenValidationResponse validationResponse = TokenValidationResponse.builder()
                    .valid(true)
                    .userId(UUID.fromString(userId))
                    .role(Role.valueOf(role))
                    .build();

            return ResponseEntity.ok(ApiResponse.successResponse("Token valid", validationResponse));

        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.successResponse("Token invalid",
                    TokenValidationResponse.builder().valid(false).build()));
        }
    }
}