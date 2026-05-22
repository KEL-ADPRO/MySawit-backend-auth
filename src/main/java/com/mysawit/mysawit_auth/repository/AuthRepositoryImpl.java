package com.mysawit.mysawit_auth.repository;

import com.mysawit.mysawit_auth.model.Role;
import com.mysawit.mysawit_auth.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@NoArgsConstructor
public class AuthRepositoryImpl implements AuthRepository {
    private static final String SELECT_USER = "SELECT u FROM User u ";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public User save(final User user) {
        return entityManager.merge(user);
    }

    @Override
    public User findByEmail(final String email) {
        List<User> results = entityManager.createQuery(
                        SELECT_USER +
                                "WHERE u.email = :email"
                        , User.class)
                .setParameter("email", email)
                .getResultList();
        return results.isEmpty() ? null : results.getFirst();
    }

    @Override
    public User findById(final UUID userId) {
        List<User> results = entityManager.createQuery(
                        SELECT_USER +
                                "WHERE u.id = :userId"
                        , User.class)
                .setParameter("userId", userId)
                .getResultList();
        return results.isEmpty() ? null : results.getFirst();
    }

    @Override
    public User findByGoogleId(final String googleId) {
        List<User> results = entityManager.createQuery(
                        SELECT_USER +
                                "WHERE u.googleId = :googleId"
                        , User.class)
                .setParameter("googleId", googleId)
                .getResultList();
        return results.isEmpty() ? null : results.getFirst();
    }

    @Override
    public User findByUsername(final String username) {
        List<User> results = entityManager.createQuery(
                        SELECT_USER +
                                "WHERE u.username = :username"
                        , User.class)
                .setParameter("username", username)
                .getResultList();
        return results.isEmpty() ? null : results.getFirst();
    }

    @Override
    public List<User> findAll() {
        return entityManager.createQuery(SELECT_USER, User.class).getResultList();
    }

    @Override
    public List<User> findByName(final String name) {
        return entityManager.createQuery(
                        SELECT_USER +
                                "WHERE LOWER(u.name) LIKE LOWER(:name)"
                        , User.class)
                .setParameter("name", "%" + name + "%")
                .getResultList();
    }

    @Override
    public List<User> findByRole(final Role role) {
        return entityManager.createQuery(
                        SELECT_USER +
                                "WHERE u.role = :role"
                        , User.class)
                .setParameter("role", role)
                .getResultList();
    }

    @Override
    public List<User> findByMandorId(UUID mandorId) {
        return entityManager.createQuery(
                SELECT_USER +
                        "WHERE u.mandorId = :mandorid",
                User.class)
                .setParameter("mandorid", mandorId)
                .getResultList();
    }

    @Override
    public void delete(final UUID userId) {
        final User user = findById(userId);
        if (user != null) {
            entityManager.remove(user);
        }
    }
}