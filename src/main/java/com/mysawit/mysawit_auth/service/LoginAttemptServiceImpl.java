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
    public void recordSuccess(final String email) {
        loginAttemptRepository.deleteByEmail(email);
    }

    @Override
    public void recordFailure(final String email) {
        final LoginAttempt loginAttempt = loginAttemptRepository.findByEmail(email);
        final LoginAttempt currentAttempt;

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
    public boolean isLocked(final String email) {
        final LoginAttempt attempt = loginAttemptRepository.findByEmail(email);

        if (attempt == null || attempt.getLockedUntil() == null) {
            return false;
        }

        if (Instant.now().isBefore(attempt.getLockedUntil())) {
            return true;
        }

        loginAttemptRepository.deleteByEmail(email);
        return false;
    }

    @Override
    public void assertNotLocked(final String email) {
        if (isLocked(email)) {
            final LoginAttempt attempt = loginAttemptRepository.findByEmail(email);
            throw new AccountLockedException(attempt.getLockedUntil());
        }
    }
}
