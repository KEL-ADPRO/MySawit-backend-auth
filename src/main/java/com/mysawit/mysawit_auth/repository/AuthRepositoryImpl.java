package com.mysawit.mysawit_auth.repository;

import com.mysawit.mysawit_auth.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@NoArgsConstructor
public class AuthRepositoryImpl implements AuthRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public User save(final User user) {
        return entityManager.merge(user);
    }

    @Override
    public User findByEmail(final String email) {
        List<User> results = entityManager.createQuery(
                        "SELECT u FROM User u " +
                                "WHERE u.email = :email"
                        , User.class)
                .setParameter("email", email)
                .getResultList();
        return results.isEmpty() ? null : results.getFirst();
    }

    @Override
    public User findById(final UUID userId) {
        List<User> results = entityManager.createQuery(
                        "SELECT u FROM User u " +
                                "WHERE u.id = :userId"
                        , User.class)
                .setParameter("userId", userId)
                .getResultList();
        return results.isEmpty() ? null : results.getFirst();
    }

    @Override
    public User findByGoogleId(final String googleId) {
        List<User> results = entityManager.createQuery(
                        "SELECT u FROM User u " +
                                "WHERE u.googleId = :googleId"
                        , User.class)
                .setParameter("googleId", googleId)
                .getResultList();
        return results.isEmpty() ? null : results.getFirst();
    }

    @Override
    public User findByUsername(final String username) {
        List<User> results = entityManager.createQuery(
                        "SELECT u FROM User u " +
                                "WHERE u.username = :username"
                        , User.class)
                .setParameter("username", username)
                .getResultList();
        return results.isEmpty() ? null : results.getFirst();
    }
}