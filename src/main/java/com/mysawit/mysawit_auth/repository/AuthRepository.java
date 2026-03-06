package com.mysawit.mysawit_auth.repository;

import com.mysawit.mysawit_auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AuthRepository {
    User save(User user);
    User findByEmail(String email);
    User findById(String userId);
    User findByUsername(String username);
}
