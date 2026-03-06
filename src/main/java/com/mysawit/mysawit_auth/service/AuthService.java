package com.mysawit.mysawit_auth.service;

import com.mysawit.mysawit_auth.util.AuthResponse;
import com.mysawit.mysawit_auth.util.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
}