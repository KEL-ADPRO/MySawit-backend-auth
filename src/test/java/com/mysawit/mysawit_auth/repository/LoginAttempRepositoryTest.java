package com.mysawit.mysawit_auth.repository;

import com.mysawit.mysawit_auth.model.LoginAttempt;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoginAttempRepositoryTest {
    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<LoginAttempt> typedQuery;

    @InjectMocks
    private LoginAttemptRepositoryImpl loginAttemptRepository;

    private LoginAttempt loginAttempt;

    @BeforeEach
    void setup() {
        loginAttempt = LoginAttempt.builder()
                        .email("user@gmail.com")
                        .failedCount(3)
                        .lastFailedAt(Instant.now())
                        .build();
    }

    @Test
    void saveCheck() {
        when(entityManager.merge(loginAttempt)).thenReturn(loginAttempt);

        final LoginAttempt result = loginAttemptRepository.save(loginAttempt);

        assertEquals(loginAttempt, result);
        verify(entityManager, times(1)).merge(loginAttempt);
    }

    @Test
    void findByEmailExists() {
        when(entityManager.createQuery(any(String.class), eq(LoginAttempt.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("email", "user@gmail.com")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(loginAttempt));

        LoginAttempt result = loginAttemptRepository.findByEmail("user@gmail.com");

        assertNotNull(result);
        assertEquals(3, result.getFailedCount());
    }

    @Test
    void findByEmailNotExists() {
        when(entityManager.createQuery(any(String.class), eq(LoginAttempt.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("email", "nonexistent@gmail.com")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of());

        LoginAttempt result = loginAttemptRepository.findByEmail("nonexistent@gmail.com");

        assertNull(result);
    }

    @Test
    void deleteByEmailExists() {
        when(entityManager.createQuery(any(String.class), eq(LoginAttempt.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("email", "user@gmail.com")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(loginAttempt));

        loginAttemptRepository.deleteByEmail("user@gmail.com");

        verify(entityManager, times(1)).remove(loginAttempt);
    }

    @Test
    void deleteByEmailNotExists() {
        when(entityManager.createQuery(any(String.class), eq(LoginAttempt.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("email", "user@gmail.com")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of());

        loginAttemptRepository.deleteByEmail("user@gmail.com");

        verify(entityManager, times(0)).remove(loginAttempt);
    }
}
