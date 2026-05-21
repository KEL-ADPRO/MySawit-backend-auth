package com.mysawit.mysawit_auth.service;

import com.mysawit.mysawit_auth.model.RefreshToken;
import com.mysawit.mysawit_auth.model.User;

public class RefreshTokenServiceImpl implements RefreshTokenService {
    @Override
    public RefreshToken createRefreshToken(User user) {
        return null;
    }

    @Override
    public RefreshToken validateAndGet(String rawToken) {
        return null;
    }

    @Override
    public void revokeAll(User user) {

    }
}