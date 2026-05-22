package com.mysawit.mysawit_auth.repository;

import com.mysawit.mysawit_auth.model.Role;
import com.mysawit.mysawit_auth.model.User;

import java.util.List;
import java.util.UUID;

public interface AuthRepository {
    User save(User user);
    User findByEmail(String email);
    User findById(UUID userId);
    User findByGoogleId(String googleId);
    User findByUsername(String username);
    List<User> findByName(String name);
    List<User> findByRole(Role role);
    void delete(UUID userId);
}
