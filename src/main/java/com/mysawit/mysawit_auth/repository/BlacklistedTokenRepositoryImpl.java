package com.mysawit.mysawit_auth.repository;

import com.mysawit.mysawit_auth.model.BlacklistedToken;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
@NoArgsConstructor
public class BlacklistedTokenRepositoryImpl implements BlacklistedTokenRepository {
    @Override
    public void save(BlacklistedToken token) {

    }

    @Override
    public boolean existsByTokenAndExpiresAtAfter(String token, Instant now) {
        return false;
    }

    @Override
    public void deleteExpired(Instant now) {

    }
}
