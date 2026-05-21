package com.mysawit.mysawit_auth.repository;

import com.mysawit.mysawit_auth.model.RefreshToken;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenRepositoryTest {
    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<RefreshToken> typedQuery;

    @InjectMocks
    private RefreshTokenRepositoryImpl refreshTokenRepository;

    private RefreshToken refreshToken;
    private final UUID USER_ID = UUID.fromString("eb558e9f-1c39-460e-8860-71af6af63bd6");
    private final String TOKEN_VALUE = "refresh.token.value";

    @BeforeEach
    void setUp() {
        refreshToken = RefreshToken.builder()
                .id(UUID.randomUUID())
                .userId(USER_ID)
                .token(TOKEN_VALUE)
                .expiresAt(Instant.now().plusSeconds(10))
                .createdAt(Instant.now())
                .isRevoked(false)
                .build();
    }

    @Test
    void savePersistsToken() {
        when(entityManager.merge(refreshToken)).thenReturn(refreshToken);

        final RefreshToken result = refreshTokenRepository.save(refreshToken);

        assertEquals(refreshToken, result);
        verify(entityManager, times(1)).merge(refreshToken);
    }

    @Test
    void findByTokenSuccess() {
        when(entityManager.createQuery(any(String.class), eq(RefreshToken.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("token", TOKEN_VALUE)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(refreshToken));

        final RefreshToken result = refreshTokenRepository.findByToken(TOKEN_VALUE);

        assertNotNull(result);
        assertEquals(TOKEN_VALUE, result.getToken());
        verify(entityManager).createQuery(any(String.class), eq(RefreshToken.class));
    }

    @Test
    void findByTokenNotFound() {
        when(entityManager.createQuery(any(String.class), eq(RefreshToken.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("token", "nonexistent.token")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of());

        final RefreshToken result = refreshTokenRepository.findByToken("nonexistent.token");

        assertNull(result);
    }

    @Test
    void findByUserIdSuccess() {
        when(entityManager.createQuery(any(String.class), eq(RefreshToken.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("userId", USER_ID)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(refreshToken));

        final List<RefreshToken> results = refreshTokenRepository.findByUserId(USER_ID);

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(USER_ID, results.getFirst().getUserId());
    }

    @Test
    void findByUserIdEmpty() {
        UUID id = UUID.randomUUID();
        when(entityManager.createQuery(any(String.class), eq(RefreshToken.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("userId", id)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of());

        final List<RefreshToken> results = refreshTokenRepository.findByUserId(id);

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    void findValidTokenByUserIdAndTokenSuccess() {
        final Instant now = Instant.now();
        when(entityManager.createQuery(any(String.class), eq(RefreshToken.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("userId", USER_ID)).thenReturn(typedQuery);
        when(typedQuery.setParameter("token", TOKEN_VALUE)).thenReturn(typedQuery);
        when(typedQuery.setParameter("now", now)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(refreshToken));

        final RefreshToken result = refreshTokenRepository.findValidByUserIdAndToken(USER_ID, TOKEN_VALUE, now);

        assertNotNull(result);
        assertEquals(USER_ID, result.getUserId());
        assertEquals(TOKEN_VALUE, result.getToken());
    }

    @Test
    void findValidTokenByUserIdAndTokenNotFound() {
        final Instant now = Instant.now();
        when(entityManager.createQuery(any(String.class), eq(RefreshToken.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("userId", USER_ID)).thenReturn(typedQuery);
        when(typedQuery.setParameter("token", "wrong.token")).thenReturn(typedQuery);
        when(typedQuery.setParameter("now", now)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of());

        final RefreshToken result = refreshTokenRepository.findValidByUserIdAndToken(USER_ID, "wrong.token", now);

        assertNull(result);
    }

    @Test
    void revokeTokenSuccess() {
        when(entityManager.createQuery(any(String.class), eq(RefreshToken.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("token", TOKEN_VALUE)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(refreshToken));

        refreshTokenRepository.revokeByToken(TOKEN_VALUE);

        verify(entityManager).merge(any(RefreshToken.class));
    }

    @Test
    void revokeAllByUserIdSuccess() {
        final RefreshToken token1 = RefreshToken.builder()
                .userId(USER_ID)
                .token("token.1")
                .expiresAt(Instant.now().plusSeconds(10))
                .createdAt(Instant.now())
                .isRevoked(false)
                .build();

        final RefreshToken token2 = RefreshToken.builder()
                .userId(USER_ID)
                .token("token.2")
                .expiresAt(Instant.now().plusSeconds(15))
                .createdAt(Instant.now())
                .isRevoked(false)
                .build();

        when(entityManager.createQuery(any(String.class), eq(RefreshToken.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("userId", USER_ID)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(token1, token2));

        refreshTokenRepository.revokeAllByUserId(USER_ID);

        verify(entityManager, times(2)).merge(any(RefreshToken.class));
    }

    @Test
    void deleteExpiredSuccess() {
        Instant now = Instant.now();
        when(entityManager.createQuery(any(String.class), eq(RefreshToken.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("now", now)).thenReturn(typedQuery);

        refreshTokenRepository.deleteExpired(now);

        verify(entityManager, times(1)).createQuery(any(String.class), eq(RefreshToken.class));
    }
}