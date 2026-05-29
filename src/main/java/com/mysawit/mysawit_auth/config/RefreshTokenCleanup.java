package com.mysawit.mysawit_auth.config;

import com.mysawit.mysawit_auth.repository.RefreshTokenRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class RefreshTokenCleanup {
    private final RefreshTokenRepository tokenRepository;

    @Scheduled(cron = "${app.refresh.cleanup-cron:0 0 * * * *}")
    @Transactional
    public void deleteExpiredTokens() {
        tokenRepository.deleteExpired(Instant.now());
    }
}