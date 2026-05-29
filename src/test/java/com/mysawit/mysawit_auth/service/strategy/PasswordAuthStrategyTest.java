package com.mysawit.mysawit_auth.service.strategy;

import com.mysawit.mysawit_auth.dto.request.AuthRequest;
import com.mysawit.mysawit_auth.dto.response.AuthResponse;
import com.mysawit.mysawit_auth.exception.InvalidCredentialException;
import com.mysawit.mysawit_auth.mapper.AuthResponseMapper;
import com.mysawit.mysawit_auth.model.RefreshToken;
import com.mysawit.mysawit_auth.model.Role;
import com.mysawit.mysawit_auth.model.User;
import com.mysawit.mysawit_auth.repository.AuthRepository;
import com.mysawit.mysawit_auth.service.LoginAttemptService;
import com.mysawit.mysawit_auth.service.RefreshTokenService;
import com.mysawit.mysawit_auth.util.JwtUtil;
import com.mysawit.mysawit_auth.util.PasswordHasher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PasswordAuthStrategyTest {
    @Mock
    private AuthRepository authRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthResponseMapper responseMapper;

    @Mock
    private LoginAttemptService loginAttemptService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private PasswordAuthStrategy passwordAuthStrategy;

    private User adminUser;

    @BeforeEach
    void setUp() {
        adminUser = User.builder()
                .username("Admin Sawit")
                .name("Agus")
                .email("admin@gmail.com")
                .password("hashed_admin123")
                .role(Role.ADMIN)
                .build();
    }

    @Test
    void authenticateSuccess() {
        final AuthResponse loginResponse = AuthResponse.builder()
                .token("dummy.jwt.token")
                .refreshToken("dummy.refresh.token")
                .username("Admin Sawit")
                .name("Agus")
                .email("admin@gmail.com")
                .role(Role.ADMIN)
                .build();

        final RefreshToken refreshToken = RefreshToken.builder().token("dummy.refresh.token").build();

        when(authRepository.findByEmail("admin@gmail.com")).thenReturn(Optional.ofNullable(adminUser));
        when(passwordHasher.matches("admin123", "hashed_admin123")).thenReturn(true);
        when(jwtUtil.generateToken(adminUser.getId(), adminUser.getRole())).thenReturn("dummy.jwt.token");
        when(refreshTokenService.createRefreshToken(adminUser)).thenReturn(refreshToken);
        when(responseMapper.toResponse(any(User.class), any(), any())).thenReturn(loginResponse);

        final AuthRequest request = AuthRequest.builder()
                .email("admin@gmail.com")
                .password("admin123")
                .build();

        final AuthResponse response = passwordAuthStrategy.authenticate(request);

        assertNotNull(response);
        assertEquals("dummy.jwt.token", response.getToken());
        assertEquals("Admin Sawit", response.getUsername());
        assertEquals("Agus", response.getName());
        assertEquals("admin@gmail.com", response.getEmail());
        assertEquals(Role.ADMIN, response.getRole());

        verify(authRepository, times(1)).findByEmail("admin@gmail.com");
        verify(jwtUtil, times(1)).generateToken(adminUser.getId(), adminUser.getRole());
        verify(responseMapper).toResponse(any(User.class), eq("dummy.jwt.token"), any());
        verify(loginAttemptService).recordSuccess("admin@gmail.com");
        verify(loginAttemptService, never()).recordFailure(any());
    }

    @Test
    void authenticateNullRequest() {
        assertThrows(InvalidCredentialException.class, () -> passwordAuthStrategy.authenticate(null));
    }

    @Test
    void authenticateUnknownEmail() {
        when(authRepository.findByEmail("unknownUser@gmail.com")).thenReturn( Optional.empty());

        final AuthRequest request = AuthRequest.builder()
                .email("unknownUser@gmail.com")
                .password("unknownPassword")
                .build();

        assertThrows(InvalidCredentialException.class, () -> passwordAuthStrategy.authenticate(request));

        verify(jwtUtil, never()).generateToken(any(), any());
        verify(responseMapper, never()).toResponse(any(), any(), any());
        verify(loginAttemptService).recordFailure("unknownUser@gmail.com");
        verify(loginAttemptService, never()).recordSuccess(any());
    }

    @Test
    void authenticateBlankEmail() {
        final AuthRequest request = AuthRequest.builder()
                .email("  ")
                .password("admin123")
                .build();

        assertThrows(InvalidCredentialException.class, () -> passwordAuthStrategy.authenticate(request));

        verify(jwtUtil, never()).generateToken(any(), any());
        verify(responseMapper, never()).toResponse(any(), any(), any());
    }

    @Test
    void authenticateNullEmail() {
        final AuthRequest request = AuthRequest.builder()
                .email(null)
                .password("admin123")
                .build();

        assertThrows(InvalidCredentialException.class, () -> passwordAuthStrategy.authenticate(request));

        verify(jwtUtil, never()).generateToken(any(), any());
        verify(responseMapper, never()).toResponse(any(), any(), any());
    }

    @Test
    void authenticateBlankPassword() {
        final AuthRequest request = AuthRequest.builder()
                .email("admin@gmail.com")
                .password("")
                .build();

        assertThrows(InvalidCredentialException.class, () -> passwordAuthStrategy.authenticate(request));

        verify(jwtUtil, never()).generateToken(any(), any());
        verify(responseMapper, never()).toResponse(any(), any(), any());
    }

    @Test
    void authenticateNullPassword() {
        final AuthRequest request = AuthRequest.builder()
                .email("admin@gmail.com")
                .password(null)
                .build();

        assertThrows(InvalidCredentialException.class, () -> passwordAuthStrategy.authenticate(request));

        verify(jwtUtil, never()).generateToken(any(), any());
        verify(responseMapper, never()).toResponse(any(), any(), any());
    }

    @Test
    void authenticateWrongPassword() {
        when(authRepository.findByEmail("admin@gmail.com")).thenReturn(Optional.ofNullable(adminUser));
        when(passwordHasher.matches("wrongPassword", "hashed_admin123")).thenReturn(false);

        final AuthRequest request = AuthRequest.builder()
                .email("admin@gmail.com")
                .password("wrongPassword")
                .build();

        assertThrows(InvalidCredentialException.class, () -> passwordAuthStrategy.authenticate(request));

        verify(jwtUtil, never()).generateToken(any(), any());
        verify(responseMapper, never()).toResponse(any(), any(), any());
        verify(loginAttemptService).recordFailure("admin@gmail.com");
        verify(loginAttemptService, never()).recordSuccess(any());
    }

    @Test
    void authenticateWithGoogleOnlyAccount() {
        final User googleOnlyUser = User.builder()
                .email("google@gmail.com")
                .password(null)
                .googleId("google-id-12345")
                .role(Role.BURUH)
                .username("google@gmail.com")
                .name("Google User")
                .build();

        when(authRepository.findByEmail("google@gmail.com")).thenReturn(Optional.ofNullable(googleOnlyUser));

        final AuthRequest request = AuthRequest.builder()
                .email("google@gmail.com")
                .password("somepassword")
                .build();

        assertThrows(IllegalArgumentException.class, () -> passwordAuthStrategy.authenticate(request));

        verify(jwtUtil, never()).generateToken(any(), any());
        verify(responseMapper, never()).toResponse(any(), any(), any());
    }

    @Test
    void authenticateWithNoGoogleIdAndNoPasswordThrows() {
        final User corruptedUser = User.builder()
                .email("broken@gmail.com")
                .googleId(null)
                .password(null)
                .role(Role.BURUH)
                .username("broken@gmail.com")
                .name("Broken User")
                .build();

        when(authRepository.findByEmail("broken@gmail.com")).thenReturn(Optional.ofNullable(corruptedUser));

        final AuthRequest request = AuthRequest.builder()
                .email("broken@gmail.com")
                .password("anything")
                .build();

        assertThrows(InvalidCredentialException.class, () -> passwordAuthStrategy.authenticate(request));

        verify(jwtUtil, never()).generateToken(any(), any());
        verify(responseMapper, never()).toResponse(any(), any(), any());
    }
}
