package com.mysawit.mysawit_auth.repository;

import com.mysawit.mysawit_auth.model.Role;
import com.mysawit.mysawit_auth.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@NoArgsConstructor
public class AuthRepositoryImpl implements AuthRepository {
    private static final String SELECT_USER = "SELECT u FROM User u ";
    private static final String PARAM_EMAIL = "email";
    private static final String PARAM_NAME = "name";
    private static final String PARAM_ROLE = "role";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public User save(final User user) {
        return entityManager.merge(user);
    }

    @Override
    @Transactional
    public void delete(final UUID userId) {
        final User user = findById(userId);
        if (user != null) {
            entityManager.remove(user);
        }
    }

    @Override
    public User findById(final UUID userId) {
        final List<User> results = entityManager.createQuery(
                        SELECT_USER + "WHERE u.id = :userId", User.class)
                .setParameter("userId", userId)
                .getResultList();
        return results.isEmpty() ? null : results.getFirst();
    }

    @Override
    public Optional<User> findByEmail(final String email) {
        final List<User> results = entityManager.createQuery(
                        SELECT_USER + "WHERE u.email = :email", User.class)
                .setParameter(PARAM_EMAIL, email)
                .getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.getFirst());
    }

    @Override
    public User findByGoogleId(final String googleId) {
        final List<User> results = entityManager.createQuery(
                        SELECT_USER + "WHERE u.googleId = :googleId", User.class)
                .setParameter("googleId", googleId)
                .getResultList();
        return results.isEmpty() ? null : results.getFirst();
    }

    @Override
    public User findByUsername(final String username) {
        final List<User> results = entityManager.createQuery(
                        SELECT_USER + "WHERE u.username = :username", User.class)
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
                        SELECT_USER + "WHERE LOWER(u.name) LIKE LOWER(:name)", User.class)
                .setParameter(PARAM_NAME, "%" + name + "%")
                .getResultList();
    }

    @Override
    public List<User> findByRole(final Role role) {
        return entityManager.createQuery(
                        SELECT_USER + "WHERE u.role = :role", User.class)
                .setParameter(PARAM_ROLE, role)
                .getResultList();
    }

    @Override
    public List<User> findByMandorId(final UUID mandorId) {
        return entityManager.createQuery(
                        SELECT_USER + "WHERE u.mandorId = :mandorId", User.class)
                .setParameter("mandorId", mandorId)
                .getResultList();
    }

    @Override
    public List<User> findByNameAndEmail(final String name, final String email) {
        return entityManager.createQuery(
                        SELECT_USER +
                                "WHERE LOWER(u.name) LIKE LOWER(:name) " +
                                "AND u.email = :email",
                        User.class)
                .setParameter(PARAM_NAME, "%" + name + "%")
                .setParameter(PARAM_EMAIL, email)
                .getResultList();
    }

    @Override
    public List<User> findByNameAndRole(final String name, final Role role) {
        return entityManager.createQuery(
                        SELECT_USER +
                                "WHERE LOWER(u.name) LIKE LOWER(:name) " +
                                "AND u.role = :role",
                        User.class)
                .setParameter(PARAM_NAME, "%" + name + "%")
                .setParameter(PARAM_ROLE, role)
                .getResultList();
    }

    @Override
    public List<User> findByEmailAndRole(final String email, final Role role) {
        return entityManager.createQuery(
                        SELECT_USER +
                                "WHERE u.email = :email " +
                                "AND u.role = :role",
                        User.class)
                .setParameter(PARAM_EMAIL, email)
                .setParameter(PARAM_ROLE, role)
                .getResultList();
    }

    @Override
    public List<User> findByNameAndEmailAndRole(final String name, final String email, final Role role) {
        return entityManager.createQuery(
                        SELECT_USER +
                                "WHERE LOWER(u.name) LIKE LOWER(:name) " +
                                "AND u.email = :email " +
                                "AND u.role = :role",
                        User.class)
                .setParameter(PARAM_NAME, "%" + name + "%")
                .setParameter(PARAM_EMAIL, email)
                .setParameter(PARAM_ROLE, role)
                .getResultList();
    }
}