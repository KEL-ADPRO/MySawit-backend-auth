package com.mysawit.mysawit_auth.repository;

import com.mysawit.mysawit_auth.model.LoginAttempt;

public interface LoginAttemptRepository {
    LoginAttempt save(LoginAttempt attempt);
    LoginAttempt findByEmail(String email);
    void deleteByEmail(String email);
}
