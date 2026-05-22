package com.mysawit.mysawit_auth.service;

import com.mysawit.mysawit_auth.model.RefreshToken;
import com.mysawit.mysawit_auth.model.User;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(User user);
    RefreshToken validateAndGet(String rawToken);
    void revokeAll(User user);
}