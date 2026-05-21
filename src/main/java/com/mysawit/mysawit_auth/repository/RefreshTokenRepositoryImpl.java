package com.mysawit.mysawit_auth.repository;

import com.mysawit.mysawit_auth.model.RefreshToken;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
@NoArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public RefreshToken save(final RefreshToken token) {
        return entityManager.merge(token);
    }

    @Override
    public RefreshToken findByToken(String token) {
        final List<RefreshToken> results = entityManager.createQuery(
                "SELECT r FROM RefreshToken r " +
                        "WHERE r.token = :token",
                        RefreshToken.class)
                .setParameter("token", token)
                .getResultList();
        return results.isEmpty() ? null : results.getFirst();
    }

    @Override
    public List<RefreshToken> findByUserId(UUID userId) {
        return entityManager.createQuery(
                        "SELECT r FROM RefreshToken r " +
                                "WHERE r.userId = :userId",
                        RefreshToken.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    public RefreshToken findValidByUserIdAndToken(UUID userId, String token, Instant now) {
        final List<RefreshToken> results = entityManager.createQuery(
                        "SELECT r FROM RefreshToken r " +
                                "WHERE r.userId = :userId " +
                                "AND r.token = :token " +
                                "AND r.expiresAt > :now " +
                                "AND r.isRevoked = false",
                        RefreshToken.class)
                .setParameter("userId", userId)
                .setParameter("token", token)
                .setParameter("now", now)
                .getResultList();
        return results.isEmpty() ? null : results.getFirst();
    }

    @Override
    public void revokeByToken(String token) {
        final RefreshToken refreshToken = findByToken(token);
        if (refreshToken != null) {
            refreshToken.setRevoked(true);
            entityManager.merge(refreshToken);
        }
    }

    @Override
    public void revokeAllByUserId(UUID userId) {
        final List<RefreshToken> tokens = findByUserId(userId);
        for (final RefreshToken token : tokens) {
            token.setRevoked(true);
            entityManager.merge(token);
        }
    }

    @Override
    public void deleteExpired(Instant now) {
        entityManager.createQuery(
                        "DELETE FROM RefreshToken r " +
                                "WHERE r.expiresAt < :now",
                        RefreshToken.class)
                .setParameter("now", now)
                .executeUpdate();
    }
}
