package com.mysawit.mysawit_auth.config;

import com.mysawit.mysawit_auth.repository.BlacklistedTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BlacklistedTokenCleanupTest {
    @Mock
    private BlacklistedTokenRepository tokenRepository;

    @InjectMocks
    private BlacklistedTokenCleanup tokenCleanup;

    @Test
    void deleteExpiredTokens_CallsRepository() {
        tokenCleanup.deleteExpiredTokens();

        verify(tokenRepository, times(1)).deleteExpired(any(Instant.class));
    }

    @Test
    void deleteExpiredTokens_PassesCurrentOrPastInstant() {
        final Instant before = Instant.now();

        tokenCleanup.deleteExpiredTokens();

        final ArgumentCaptor<Instant> captor = ArgumentCaptor.forClass(Instant.class);
        verify(tokenRepository).deleteExpired(captor.capture());

        final Instant captured = captor.getValue();
        assertFalse(captured.isAfter(Instant.now()), "Instant passed to deleteExpired must not be in the future");
        assertFalse(captured.isBefore(before), "Instant passed to deleteExpired must not be before the test started");
    }

    @Test
    void deleteExpiredTokens_DoesntCallAnyOtherRepositoryMethod() {
        tokenCleanup.deleteExpiredTokens();

        verify(tokenRepository, times(1)).deleteExpired(any(Instant.class));
        verify(tokenRepository, never()).save(any());
        verify(tokenRepository, never()).isTokenBlacklisted(any(), any());
        verifyNoMoreInteractions(tokenRepository);
    }

    @Test
    void deleteExpiredTokens_CanBeCalledMultipleTimes() {
        tokenCleanup.deleteExpiredTokens();
        tokenCleanup.deleteExpiredTokens();
        tokenCleanup.deleteExpiredTokens();

        verify(tokenRepository, times(3)).deleteExpired(any(Instant.class));
    }

    @Test
    void deleteExpiredTokens_EachCallPassesItsOwnInstant() {
        tokenCleanup.deleteExpiredTokens();
        tokenCleanup.deleteExpiredTokens();

        final ArgumentCaptor<Instant> captor = ArgumentCaptor.forClass(Instant.class);
        verify(tokenRepository, times(2)).deleteExpired(captor.capture());

        final Instant first = captor.getAllValues().get(0);
        final Instant second = captor.getAllValues().get(1);
        assertFalse(second.isBefore(first), "Second call instant must not be earlier than first call instant");
    }
}