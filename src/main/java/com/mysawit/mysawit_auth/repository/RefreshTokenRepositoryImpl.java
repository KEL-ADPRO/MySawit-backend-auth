package com.mysawit.mysawit_auth.repository;

import com.mysawit.mysawit_auth.model.RefreshToken;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {
    @Override
    public RefreshToken save(RefreshToken token) {
        return null;
    }

    @Override
    public RefreshToken findByToken(String token) {
        return null;
    }

    @Override
    public List<RefreshToken> findByUserId(UUID userId) {
        return List.of();
    }

    @Override
    public RefreshToken findValidByUserIdAndToken(UUID userId, String token, Instant now) {
        return null;
    }

    @Override
    public void revokeByToken(String token) {

    }

    @Override
    public void revokeAllByUserId(UUID userId) {

    }

    @Override
    public void deleteExpired(Instant now) {

    }
}
