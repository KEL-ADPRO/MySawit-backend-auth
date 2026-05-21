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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
                .build();
    }

    @Test
    void saveSuccess() {
        when(entityManager.merge(refreshToken)).thenReturn(refreshToken);

        final RefreshToken savedToken = refreshTokenRepository.save(refreshToken);

        assertNotNull(savedToken);
        assertEquals(TOKEN_VALUE, savedToken.getToken());
        verify(entityManager, times(1)).merge(refreshToken);
    }

    @Test
    void findByTokenExists() {
        when(entityManager.createQuery(any(String.class), eq(RefreshToken.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("token", TOKEN_VALUE)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(refreshToken));

        final RefreshToken result = refreshTokenRepository.findByToken(TOKEN_VALUE);

        assertNotNull(result);
        assertEquals(TOKEN_VALUE, result.getToken());
        assertEquals(USER_ID, result.getUserId());

        verify(entityManager, times(1)).createQuery(any(String.class), eq(RefreshToken.class));
        verify(typedQuery, times(1)).setParameter("token", TOKEN_VALUE);
    }

    @Test
    void findByTokenNotExist() {
        when(entityManager.createQuery(any(String.class), eq(RefreshToken.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("token", "wrong.token")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of());

        final RefreshToken result = refreshTokenRepository.findByToken("wrong.token");

        assertNull(result);

        verify(entityManager, times(1)).createQuery(any(String.class), eq(RefreshToken.class));
        verify(typedQuery, times(1)).setParameter("token", "wrong.token");
    }

    @Test
    void deleteByUserIdSuccess() {
        when(entityManager.createQuery(any(String.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("userId", USER_ID)).thenReturn(typedQuery);
        when(typedQuery.executeUpdate()).thenReturn(1);

        refreshTokenRepository.deleteByUserId(USER_ID);

        verify(entityManager, times(1)).createQuery(any(String.class));
        verify(typedQuery, times(1)).setParameter("userId", USER_ID);
        verify(typedQuery, times(1)).executeUpdate();
    }

    @Test
    void deleteByUserIdUnknown() {
        UUID unknownId = UUID.fromString("fc558e9f-1c39-460e-8860-71af6af63bd6");
        when(entityManager.createQuery(any(String.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("userId", unknownId)).thenReturn(null);

        assertThrows(NullPointerException.class, () -> refreshTokenRepository.deleteByUserId(unknownId));

        verify(entityManager, times(1)).createQuery(any(String.class));
        verify(typedQuery, times(1)).setParameter("userId", unknownId);
        verify(typedQuery, times(0)).executeUpdate();
    }

    @Test
    void deleteExpiredSuccess() {
        final Instant now = Instant.now();
        when(entityManager.createQuery(any(String.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("now", now)).thenReturn(typedQuery);
        when(typedQuery.executeUpdate()).thenReturn(5);

        refreshTokenRepository.deleteExpired(now);

        verify(entityManager, times(1)).createQuery(any(String.class));
        verify(typedQuery, times(1)).setParameter("now", now);
        verify(typedQuery, times(1)).executeUpdate();
    }
}