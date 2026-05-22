package com.mysawit.mysawit_auth.service;

import com.mysawit.mysawit_auth.exception.AccountLockedException;
import com.mysawit.mysawit_auth.model.LoginAttempt;
import com.mysawit.mysawit_auth.repository.LoginAttemptRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoginAttemptServiceTest {
    @Mock
    private LoginAttemptRepository loginAttemptRepository;

    @InjectMocks
    private LoginAttemptServiceImpl loginAttemptService;

    private final String userEmail = "user@gmail.com";

    @BeforeEach
    void setUp() throws Exception {
        Field maxAttemptsField = LoginAttemptServiceImpl.class.getDeclaredField("maxAttempts");
        maxAttemptsField.setAccessible(true);
        maxAttemptsField.set(loginAttemptService, 5);

        Field lockDurationField = LoginAttemptServiceImpl.class.getDeclaredField("lockDurationMinutes");
        lockDurationField.setAccessible(true);
        lockDurationField.set(loginAttemptService, 15L);
    }

    @Test
    void recordSuccessCheck() {
        loginAttemptService.recordSuccess(userEmail);

        verify(loginAttemptRepository, times(1)).deleteByEmail(userEmail);
    }

    @Test
    void recordFailureCheck() {
        when(loginAttemptRepository.findByEmail(userEmail)).thenReturn(null);

        loginAttemptService.recordFailure(userEmail);

        ArgumentCaptor<LoginAttempt> captor = ArgumentCaptor.forClass(LoginAttempt.class);
        verify(loginAttemptRepository, times(1)).save(captor.capture());

        LoginAttempt savedAttempt = captor.getValue();
        assertEquals(userEmail, savedAttempt.getEmail());
        assertEquals(1, savedAttempt.getFailedCount());
        assertNotNull(savedAttempt.getLastFailedAt());
        assertNull(savedAttempt.getLockedUntil());
    }

    @Test
    void recordFailure_WhenPreviousAttemptExists_ShouldIncrementCount() {
        LoginAttempt preExistingAttempt = LoginAttempt.builder()
                .email(userEmail)
                .failedCount(2)
                .lastFailedAt(Instant.now().minusSeconds(60))
                .build();

        when(loginAttemptRepository.findByEmail(userEmail)).thenReturn(preExistingAttempt);

        loginAttemptService.recordFailure(userEmail);

        ArgumentCaptor<LoginAttempt> captor = ArgumentCaptor.forClass(LoginAttempt.class);
        verify(loginAttemptRepository, times(1)).save(captor.capture());

        LoginAttempt savedAttempt = captor.getValue();
        assertEquals(3, savedAttempt.getFailedCount());
        assertNull(savedAttempt.getLockedUntil());
    }

    @Test
    void recordFailure_WhenThresholdReached_ShouldLockAccount() {
        LoginAttempt ongoingAttempt = LoginAttempt.builder()
                .email(userEmail)
                .failedCount(4)
                .lastFailedAt(Instant.now().minusSeconds(10))
                .build();

        when(loginAttemptRepository.findByEmail(userEmail)).thenReturn(ongoingAttempt);

        loginAttemptService.recordFailure(userEmail);

        ArgumentCaptor<LoginAttempt> captor = ArgumentCaptor.forClass(LoginAttempt.class);
        verify(loginAttemptRepository, times(1)).save(captor.capture());

        LoginAttempt savedAttempt = captor.getValue();
        assertEquals(5, savedAttempt.getFailedCount());
        assertNotNull(savedAttempt.getLockedUntil());

        assertTrue(savedAttempt.getLockedUntil().isAfter(Instant.now().plusSeconds(14 * 60)));
        assertTrue(savedAttempt.getLockedUntil().isBefore(Instant.now().plusSeconds(16 * 60)));
    }

    @Test
    void isLocked_WhenNoRecord_ShouldReturnFalse() {
        when(loginAttemptRepository.findByEmail(userEmail)).thenReturn(null);

        assertFalse(loginAttemptService.isLocked(userEmail));
    }

    @Test
    void isLocked_WhenRecordNotLocked_ShouldReturnFalse() {
        LoginAttempt newAttempt = LoginAttempt.builder()
                .email(userEmail)
                .failedCount(1)
                .lockedUntil(null)
                .build();

        when(loginAttemptRepository.findByEmail(userEmail)).thenReturn(newAttempt);

        assertFalse(loginAttemptService.isLocked(userEmail));
    }

    @Test
    void isLocked_WhenLockoutExpired_ShouldReturnFalse() {
        LoginAttempt expiredLockAttempt = LoginAttempt.builder()
                .email(userEmail)
                .failedCount(5)
                .lockedUntil(Instant.now().minusSeconds(10))
                .build();

        when(loginAttemptRepository.findByEmail(userEmail)).thenReturn(expiredLockAttempt);

        assertFalse(loginAttemptService.isLocked(userEmail));
    }

    @Test
    void isLocked_WhenLockoutIsActive_ShouldReturnTrue() {
        LoginAttempt activeLockAttempt = LoginAttempt.builder()
                .email(userEmail)
                .failedCount(5)
                .lockedUntil(Instant.now().plusSeconds(900))
                .build();

        when(loginAttemptRepository.findByEmail(userEmail)).thenReturn(activeLockAttempt);

        assertTrue(loginAttemptService.isLocked(userEmail));
    }

    @Test
    void assertNotLocked_WhenAccountIsNotLocked_ShouldNotThrowException() {
        when(loginAttemptRepository.findByEmail(userEmail)).thenReturn(null);

        assertDoesNotThrow(() -> loginAttemptService.assertNotLocked(userEmail));
    }

    @Test
    void assertNotLocked_WhenAccountLockoutExpired_ShouldNotThrowException() {
        LoginAttempt expiredLockAttempt = LoginAttempt.builder()
                .email(userEmail)
                .failedCount(5)
                .lockedUntil(Instant.now().minusSeconds(5))
                .build();

        when(loginAttemptRepository.findByEmail(userEmail)).thenReturn(expiredLockAttempt);

        assertDoesNotThrow(() -> loginAttemptService.assertNotLocked(userEmail));
    }

    @Test
    void assertNotLocked_WhenAccountIsActivelyLocked_ShouldThrowAccountLockedException() {
        Instant expectedLockTime = Instant.now().plusSeconds(600);
        LoginAttempt activeLockAttempt = LoginAttempt.builder()
                .email(userEmail)
                .failedCount(5)
                .lockedUntil(expectedLockTime)
                .build();

        when(loginAttemptRepository.findByEmail(userEmail)).thenReturn(activeLockAttempt);

        AccountLockedException exception = assertThrows(AccountLockedException.class, () -> loginAttemptService.assertNotLocked(userEmail));

        assertEquals(expectedLockTime, exception.getLockedUntil());
    }
}