package com.mysawit.mysawit_auth.repository;

import com.mysawit.mysawit_auth.model.BlacklistedToken;
import java.time.Instant;

public interface BlacklistedTokenRepository {
    BlacklistedToken save(BlacklistedToken token);
    boolean existsByTokenAndExpiresAtAfter(String token, Instant now);
    void deleteExpired(Instant now);
}
