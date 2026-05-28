package com.mysawit.mysawit_auth.service;

import com.mysawit.mysawit_auth.exception.InvalidCredentialException;
import com.mysawit.mysawit_auth.model.RefreshToken;
import com.mysawit.mysawit_auth.model.User;
import com.mysawit.mysawit_auth.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private static final int TOKEN_BYTES = 32;
    private final RefreshTokenRepository refreshTokenRepository;
    private final SecureRandom secureRandom;

    @Value("${app.refresh-token.expiration-days:7}")
    private long expirationDays;

    @Override
    @Transactional
    public RefreshToken createRefreshToken(final User user) {
        final String rawToken = generateToken();
        final Instant now = Instant.now();

        final RefreshToken refreshToken = RefreshToken.builder()
                .token(rawToken)
                .userId(user.getId())
                .createdAt(now)
                .expiresAt(now.plusSeconds(expirationDays * 24 * 60 * 60))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshToken validateAndGet(final String rawToken) {
        final RefreshToken token = refreshTokenRepository.findByToken(rawToken);

        if (token == null) {
            throw new InvalidCredentialException();
        }

        if (token.getExpiresAt().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new InvalidCredentialException();
        }

        return token;
    }

    @Override
    @Transactional
    public void deleteByToken(final String token) {
        refreshTokenRepository.deleteByToken(token);
    }

    private String generateToken() {
        final byte[] bytes = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}