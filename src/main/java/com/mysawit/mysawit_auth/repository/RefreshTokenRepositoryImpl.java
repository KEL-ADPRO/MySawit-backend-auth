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
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public RefreshToken save(final RefreshToken token) {
        return entityManager.merge(token);
    }

    @Override
    public RefreshToken findByToken(final String token) {
        final List<RefreshToken> results = entityManager.createQuery(
                        "SELECT r FROM RefreshToken r " +
                                "WHERE r.token = :token",
                        RefreshToken.class)
                .setParameter("token", token)
                .getResultList();
        return results.isEmpty() ? null : results.getFirst();
    }

    @Override
    @Transactional
    public void deleteByUserId(final UUID userId) {
        entityManager.createQuery(
                "DELETE FROM RefreshToken r " +
                        "WHERE r.userId = :userId")
                .setParameter("userId", userId)
                .executeUpdate();
    }

    @Override
    @Transactional
    public void deleteExpired(final Instant now) {
        entityManager.createQuery(
                "DELETE FROM RefreshToken r " +
                        "WHERE r.expiresAt < :now")
                .setParameter("now", now)
                .executeUpdate();
    }
}
