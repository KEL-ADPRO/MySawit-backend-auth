package com.mysawit.mysawit_auth.repository;

import com.mysawit.mysawit_auth.model.RefreshToken;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
@NoArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {
    private static final String SELECT_TOKEN = "SELECT r FROM RefreshToken r ";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public RefreshToken save(final RefreshToken token) {
        return entityManager.merge(token);
    }

    @Override
    public RefreshToken findByToken(String token) {
        final List<RefreshToken> results = entityManager.createQuery(
                SELECT_TOKEN +
                        "WHERE r.token = :token",
                RefreshToken.class)
                .setParameter("token", token)
                .getResultList();
        return results.isEmpty() ? null : results.getFirst();
    }

    @Override
    public List<RefreshToken> findByUserId(UUID userId) {
        return entityManager.createQuery(
                SELECT_TOKEN +
                        "WHERE r.userId = :userId",
                RefreshToken.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    public RefreshToken findByUserIdAndToken(UUID userId, String token) {
        final List<RefreshToken> results = entityManager.createQuery(
                SELECT_TOKEN +
                        "WHERE r.userId = :userId AND r.token = :token",
                RefreshToken.class)
                .setParameter("userId", userId)
                .setParameter("token", token)
                .getResultList();
        return results.isEmpty() ? null : results.getFirst();
    }

    @Override
    @Transactional
    public void updateRevokedStatusByUserId(UUID userId, boolean revoked) {
        entityManager.createQuery(
                "UPDATE RefreshToken r " +
                        "SET r.isRevoked = :revoked " +
                        "WHERE r.userId = :userId")
                .setParameter("userId", userId)
                .setParameter("revoked", revoked)
                .executeUpdate();
    }

    @Override
    @Transactional
    public void deleteByExpiryDateBefore(Instant now) {
        entityManager.createQuery(
                "DELETE FROM RefreshToken r " +
                        "WHERE r.expiresAt < :now")
                .setParameter("now", now)
                .executeUpdate();
    }
}
