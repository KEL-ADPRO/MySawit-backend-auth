package com.mysawit.mysawit_auth.service;

import com.mysawit.mysawit_auth.dto.request.AuthRequest;
import com.mysawit.mysawit_auth.dto.response.AuthResponse;

public interface GoogleAuthService {
    AuthResponse loginOrRegister(AuthRequest request);
}