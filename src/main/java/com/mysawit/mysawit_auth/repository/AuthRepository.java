package com.mysawit.mysawit_auth.repository;

import com.mysawit.mysawit_auth.model.User;

public interface AuthRepository {
    User save(User user);
    User findByEmail(String email);
    User findById(String userId);
    User findByUsername(String username);
}
