package com.mysawit.mysawit_auth.service;

import com.mysawit.mysawit_auth.util.AuthResponse;
import com.mysawit.mysawit_auth.util.LoginRequest;
import com.mysawit.mysawit_auth.util.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}