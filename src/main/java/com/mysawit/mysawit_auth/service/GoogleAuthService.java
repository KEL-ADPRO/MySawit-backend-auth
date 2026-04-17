package com.mysawit.mysawit_auth.service;

import com.mysawit.mysawit_auth.dto.response.AuthResponse;
import com.mysawit.mysawit_auth.dto.request.GoogleAuthRequest;

public interface GoogleAuthService {
    AuthResponse loginOrRegister(GoogleAuthRequest request);
}