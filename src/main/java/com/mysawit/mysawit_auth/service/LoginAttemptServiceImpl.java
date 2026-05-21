package com.mysawit.mysawit_auth.service;

import com.mysawit.mysawit_auth.exception.AccountLockedException;
import com.mysawit.mysawit_auth.model.LoginAttempt;
import com.mysawit.mysawit_auth.repository.LoginAttemptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class LoginAttemptServiceImpl implements LoginAttemptService {
    private final LoginAttemptRepository loginAttemptRepository;

    @Value("${app.login.max-attempts:5}")
    private int maxAttempts;

    @Value("${app.login.lock-duration-minutes:15}")
    private long lockDurationMinutes;

    @Override
    public void recordSuccess(String email) {
        loginAttemptRepository.deleteByEmail(email);
    }

    @Override
    public void recordFailure(String email) {
        LoginAttempt loginAttempt = loginAttemptRepository.findByEmail(email);
        LoginAttempt currentAttempt;

        if (loginAttempt == null) {
            currentAttempt = LoginAttempt.builder()
                    .email(email)
                    .failedCount(1)
                    .lastFailedAt(Instant.now())
                    .build();
        } else {
            currentAttempt = loginAttempt;
            currentAttempt.setFailedCount(currentAttempt.getFailedCount() + 1);
            currentAttempt.setLastFailedAt(Instant.now());

            if (currentAttempt.getFailedCount() >= maxAttempts) {
                currentAttempt.setLockedUntil(Instant.now().plus(lockDurationMinutes, ChronoUnit.MINUTES));
            }
        }

        loginAttemptRepository.save(currentAttempt);
    }

    @Override
    public boolean isLocked(String email) {
        LoginAttempt loginAttempt = loginAttemptRepository.findByEmail(email);

        if (loginAttempt == null) {
            return false;
        }

        if (loginAttempt.getLockedUntil() != null) {
            if (Instant.now().isBefore(loginAttempt.getLockedUntil())) {
                return true;
            } else {
                loginAttemptRepository.deleteByEmail(email);
                return false;
            }
        }

        return false;
    }

    @Override
    public void assertNotLocked(String email) {
        LoginAttempt loginAttempt = loginAttemptRepository.findByEmail(email);
        if (loginAttempt != null && loginAttempt.getLockedUntil() != null && Instant.now().isBefore(loginAttempt.getLockedUntil())) {
            throw new AccountLockedException(loginAttempt.getLockedUntil());
        }
    }


}
