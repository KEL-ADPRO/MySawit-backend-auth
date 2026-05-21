package com.mysawit.mysawit_auth.repository;

import com.mysawit.mysawit_auth.model.LoginAttempt;

import java.util.Optional;

public interface LoginAttemptRepository {
    LoginAttempt save(LoginAttempt attempt);
    Optional<LoginAttempt> findByEmail(String email);
    void deleteByEmail(String email);
}
