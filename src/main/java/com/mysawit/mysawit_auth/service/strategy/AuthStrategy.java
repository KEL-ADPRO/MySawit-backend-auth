package com.mysawit.mysawit_auth.service.strategy;

import com.mysawit.mysawit_auth.dto.response.AuthResponse;
import com.mysawit.mysawit_auth.model.AuthProvider;

public interface AuthStrategy<T> {
    AuthResponse authenticate(T request);
    AuthProvider getProviderType();
}
