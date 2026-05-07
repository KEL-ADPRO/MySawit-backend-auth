package com.mysawit.mysawit_auth.repository;

import com.mysawit.mysawit_auth.model.BlacklistedToken;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
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
public class BlacklistedTokenRepositoryTest {
    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<BlacklistedToken> typedQuery;

    @Mock
    private Query query;

    @InjectMocks
    private BlacklistedTokenRepositoryImpl blacklistedTokenRepository;

    private BlacklistedToken blacklistedToken;

    @BeforeEach
    public void setUp() {
        blacklistedToken = new BlacklistedToken();
        blacklistedToken.setToken("blacklisted.jwt.token");
        blacklistedToken.setExpiresAt(Instant.now().plusSeconds(3600));
    }

    @Test
    void saveCheck() {
        when(entityManager.merge(blacklistedToken)).thenReturn(blacklistedToken);

        final BlacklistedToken result = blacklistedTokenRepository.save(blacklistedToken);

        assertEquals(blacklistedToken, result);
        verify(entityManager, times(1)).merge(blacklistedToken);
    }

    @Test
    void existsByToken_validToken() {
        final Instant now = Instant.now();
        when(entityManager.createQuery(any(String.class), eq(BlacklistedToken.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("token", "blacklisted.jwt.token")).thenReturn(typedQuery);
        when(typedQuery.setParameter("now", now)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(blacklistedToken));

        assertTrue(blacklistedTokenRepository.isTokenBlacklisted("blacklisted.jwt.token", now));
    }

    @Test
    void existsByToken_expiredToken() {
        final Instant now = Instant.now();
        when(entityManager.createQuery(any(String.class), eq(BlacklistedToken.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("token", "expired.jwt.token")).thenReturn(typedQuery);
        when(typedQuery.setParameter("now", now)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of());

        assertFalse(blacklistedTokenRepository.isTokenBlacklisted("expired.jwt.token", now));
    }

    @Test
    void deleteExpired_executesUpdate() {
        final Instant now = Instant.now();
        when(entityManager.createQuery(any(String.class))).thenReturn(query);
        when(query.setParameter("now", now)).thenReturn(query);
        when(query.executeUpdate()).thenReturn(1);

        blacklistedTokenRepository.deleteExpired(now);

        verify(query, times(1)).executeUpdate();
    }

    @Test
    void deleteExpired_noneExpired() {
        final Instant now = Instant.now();
        when(entityManager.createQuery(any(String.class))).thenReturn(query);
        when(query.setParameter("now", now)).thenReturn(query);
        when(query.executeUpdate()).thenReturn(0);

        blacklistedTokenRepository.deleteExpired(now);

        verify(query, times(1)).executeUpdate();
    }
}