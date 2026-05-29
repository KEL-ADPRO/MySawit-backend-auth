package com.mysawit.mysawit_auth.repository;

import com.mysawit.mysawit_auth.model.Role;
import com.mysawit.mysawit_auth.model.User;
import com.mysawit.mysawit_auth.util.UserFilter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuthRepository {
    User save(User user);
    void delete(UUID userId);
    User findById(UUID userId);
    Optional<User> findByEmail(String email);
    User findByGoogleId(String googleId);
    User findByUsername(String username);
    List<User> findAll();
    List<User> findByName(String name);
    List<User> findByRole(Role role);
    List<User> findByMandorId(UUID mandorId);
    List<User> findWithFilters(UserFilter filter);
}