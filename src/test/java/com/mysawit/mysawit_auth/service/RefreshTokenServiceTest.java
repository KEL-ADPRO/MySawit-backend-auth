package com.mysawit.mysawit_auth.service;

import com.mysawit.mysawit_auth.exception.InvalidCredentialException;
import com.mysawit.mysawit_auth.model.RefreshToken;
import com.mysawit.mysawit_auth.model.User;
import com.mysawit.mysawit_auth.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {
    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private SecureRandom secureRandom;

    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;

    private User user;
    private final UUID USER_ID = UUID.fromString("eb558e9f-1c39-460e-8860-71af6af63bd6");
    private final String VALID_RAW_TOKEN = "sample-base64-url-encoded-token-string";

    @BeforeEach
    void setUp() throws Exception {
        Field expirationDaysField = RefreshTokenServiceImpl.class.getDeclaredField("expirationDays");
        expirationDaysField.setAccessible(true);
        expirationDaysField.set(refreshTokenService, 7L);

        user = User.builder()
                .id(USER_ID)
                .email("user@mysawit.com")
                .build();
    }

    @Test
    void createRefreshTokenSuccess() {
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(i -> i.getArguments()[0]);


        final RefreshToken result = refreshTokenService.createRefreshToken(user);

        assertNotNull(result);
        assertEquals(USER_ID, result.getUserId());
        assertNotNull(result.getToken());
        assertFalse(result.getToken().isBlank());

        final Instant expectedExpiry = Instant.now().plusSeconds(7 * 24 * 60 * 60);
        assertTrue(result.getExpiresAt().isBefore(expectedExpiry.plusSeconds(5)));
        assertTrue(result.getExpiresAt().isAfter(expectedExpiry.minusSeconds(5)));

        verify(refreshTokenRepository, times(1)).deleteByUserId(USER_ID);
        verify(refreshTokenRepository, times(1)).save(result);
    }

    @Test
    void validateAndGetSuccess() {
        RefreshToken validToken = RefreshToken.builder()
                .token(VALID_RAW_TOKEN)
                .userId(USER_ID)
                .expiresAt(Instant.now().plusSeconds(10))
                .build();
        when(refreshTokenRepository.findByToken(VALID_RAW_TOKEN)).thenReturn(validToken);

        RefreshToken result = refreshTokenService.validateAndGet(VALID_RAW_TOKEN);

        assertNotNull(result);
        assertEquals(VALID_RAW_TOKEN, result.getToken());
        assertEquals(USER_ID, result.getUserId());
        verify(refreshTokenRepository, times(1)).findByToken(VALID_RAW_TOKEN);
    }

    @Test
    void validateAndGetTokenNotExist() {
        when(refreshTokenRepository.findByToken("unknown_token")).thenReturn(null);

        assertThrows(InvalidCredentialException.class, () -> refreshTokenService.validateAndGet("unknown_token"));

        verify(refreshTokenRepository, times(1)).findByToken("unknown_token");
    }

    @Test
    void validateAndGetExpiredToken() {
        RefreshToken expiredToken = RefreshToken.builder()
                .token(VALID_RAW_TOKEN)
                .userId(USER_ID)
                .expiresAt(Instant.now().minusSeconds(10))
                .build();
        when(refreshTokenRepository.findByToken(VALID_RAW_TOKEN)).thenReturn(expiredToken);

        assertThrows(InvalidCredentialException.class, () -> refreshTokenService.validateAndGet(VALID_RAW_TOKEN));

        verify(refreshTokenRepository, times(1)).findByToken(VALID_RAW_TOKEN);
    }
}