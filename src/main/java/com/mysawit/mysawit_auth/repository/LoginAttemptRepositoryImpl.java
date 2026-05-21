package com.mysawit.mysawit_auth.repository;

import com.mysawit.mysawit_auth.model.LoginAttempt;

import java.util.Optional;

public class LoginAttemptRepositoryImpl implements LoginAttemptRepository {
    @Override
    public LoginAttempt save(LoginAttempt attempt) {
        return null;
    }

    @Override
    public Optional<LoginAttempt> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public void deleteByEmail(String email) {

    }
}
