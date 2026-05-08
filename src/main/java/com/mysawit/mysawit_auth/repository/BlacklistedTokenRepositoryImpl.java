package com.mysawit.mysawit_auth.repository;

import com.mysawit.mysawit_auth.model.BlacklistedToken;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
@NoArgsConstructor
public class BlacklistedTokenRepositoryImpl implements BlacklistedTokenRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public BlacklistedToken save(final BlacklistedToken token) {
        return entityManager.merge(token);
    }

    @Override
    public boolean isTokenBlacklisted(final String token, final Instant now) {
        List<BlacklistedToken> results = entityManager.createQuery(
                        "SELECT b FROM BlacklistedToken b " +
                                "WHERE b.token = :token AND b.expiresAt > :now",
                        BlacklistedToken.class)
                .setParameter("token", token)
                .setParameter("now", now)
                .getResultList();
        return !results.isEmpty();
    }

    @Override
    public void deleteExpired(final Instant now) {
        entityManager.createQuery(
                        "DELETE FROM BlacklistedToken b WHERE b.expiresAt < :now")
                .setParameter("now", now)
                .executeUpdate();
    }
}
