package com.mysawit.mysawit_auth.repository;

import com.mysawit.mysawit_auth.model.RefreshToken;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface RefreshTokenRepository {
    RefreshToken save(RefreshToken token);
    RefreshToken findByToken(String token);
    List<RefreshToken> findByUserId(UUID userId);
    RefreshToken findValidByUserIdAndToken(UUID userId, String token, Instant now);
    void revokeByToken(String token);
    void revokeAllByUserId(UUID userId);
    void deleteExpired(Instant now);
}