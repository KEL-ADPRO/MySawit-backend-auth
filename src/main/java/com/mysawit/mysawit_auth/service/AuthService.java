package com.mysawit.mysawit_auth.service;

import com.mysawit.mysawit_auth.dto.request.AuthRequest;
import com.mysawit.mysawit_auth.dto.response.AuthResponse;
import com.mysawit.mysawit_auth.dto.request.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(AuthRequest request);
    AuthResponse getLoggedInUser(String token);
    void logout(String token);
    AuthResponse refresh(String rawRefreshToken);
}