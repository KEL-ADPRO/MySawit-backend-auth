package com.mysawit.mysawit_auth.controller;

import com.mysawit.mysawit_auth.dto.request.GoogleAuthRequest;
import com.mysawit.mysawit_auth.dto.response.ApiResponse;
import com.mysawit.mysawit_auth.dto.response.AuthResponse;
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
    @PostMapping
    public ResponseEntity<ApiResponse<AuthResponse>> loginOrRegister(@Valid @RequestBody final GoogleAuthRequest request) {
        return null;
    }
}
