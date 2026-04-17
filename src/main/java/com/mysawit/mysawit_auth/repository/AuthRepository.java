package com.mysawit.mysawit_auth.repository;

import com.mysawit.mysawit_auth.model.User;

import java.util.UUID;

public interface AuthRepository {
    User save(User user);
    User findByEmail(String email);
    User findById(UUID userId);
    User findByGoogleId(String googleId);
    User findByUsername(String username);
}
