package com.mysawit.mysawit_auth.repository;

import com.mysawit.mysawit_auth.model.LoginAttempt;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@NoArgsConstructor
public class LoginAttemptRepositoryImpl implements LoginAttemptRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public LoginAttempt save(LoginAttempt attempt) {
        return entityManager.merge(attempt);
    }

    @Override
    public LoginAttempt findByEmail(final String email) {
        final List<LoginAttempt> results = entityManager.createQuery(
                        "SELECT l FROM LoginAttempt l WHERE l.email = :email",
                        LoginAttempt.class)
                .setParameter("email", email)
                .getResultList();

        return results.isEmpty() ? null : results.getFirst();
    }

    @Override
    @Transactional
    public void deleteByEmail(final String email) {
        final LoginAttempt loginAttempt = findByEmail(email);
        if (loginAttempt != null) {
            entityManager.remove(loginAttempt);
        }
    }
}
