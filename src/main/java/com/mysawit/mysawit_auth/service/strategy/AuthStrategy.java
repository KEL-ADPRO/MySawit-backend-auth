package com.mysawit.mysawit_auth.service.strategy;

import com.mysawit.mysawit_auth.dto.request.AuthRequest;
import com.mysawit.mysawit_auth.dto.response.AuthResponse;
import com.mysawit.mysawit_auth.model.AuthProvider;

public interface AuthStrategy {
    AuthResponse authenticate(AuthRequest request);
    AuthProvider getProviderType();
}
