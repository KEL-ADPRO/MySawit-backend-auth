package com.mysawit.mysawit_auth.service;

import com.mysawit.mysawit_auth.dto.request.AuthRequest;
import com.mysawit.mysawit_auth.exception.InvalidCredentialException;
import com.mysawit.mysawit_auth.mapper.AuthResponseMapper;
import com.mysawit.mysawit_auth.model.AuthProvider;
import com.mysawit.mysawit_auth.model.RefreshToken;
import com.mysawit.mysawit_auth.model.Role;
import com.mysawit.mysawit_auth.model.User;
import com.mysawit.mysawit_auth.repository.AuthRepository;
import com.mysawit.mysawit_auth.service.strategy.AuthStrategy;
import com.mysawit.mysawit_auth.service.strategy.AuthStrategyFactory;
import com.mysawit.mysawit_auth.repository.RefreshTokenRepository;
import com.mysawit.mysawit_auth.util.*;
import com.mysawit.mysawit_auth.dto.request.RegisterRequest;
import com.mysawit.mysawit_auth.dto.response.AuthResponse;
import com.mysawit.mysawit_auth.validator.RegistrationValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private AuthRepository authRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private TokenBlacklist tokenBlacklist;

    @Mock
    private RegistrationValidator registrationValidator;

    @Mock
    private AuthResponseMapper responseMapper;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private AuthStrategyFactory strategyFactory;

    @Mock
    private AuthStrategy passwordStrategy;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest adminRequest;
    private RegisterRequest mandorRequest;
    private RegisterRequest buruhRequest;
    private RegisterRequest supirRequest;
    private User adminUser;
    private AuthResponse adminResponse;
    private AuthResponse mandorResponse;
    private AuthResponse buruhResponse;
    private AuthResponse supirResponse;

    @BeforeEach
    void setUp() {
        adminUser = User.builder()
                .username("Admin Sawit")
                .name("Agus")
                .email("admin@gmail.com")
                .password("hashed_admin123")
                .role(Role.ADMIN)
                .build();

        adminRequest = RegisterRequest.builder()
                .username("Admin Sawit")
                .name("Agus")
                .email("admin@gmail.com")
                .password("admin123")
                .role(Role.ADMIN)
                .build();

        mandorRequest = RegisterRequest.builder()
                .username("Mandor Sawit")
                .name("Burhan")
                .email("burhan@gmail.com")
                .password("mandor123")
                .role(Role.MANDOR)
                .nomorSertifMandor("CERT-001")
                .build();

        buruhRequest = RegisterRequest.builder()
                .username("Buruh Sawit")
                .name("Usep")
                .email("usep@gmail.com")
                .password("buruh123")
                .role(Role.BURUH)
                .build();

        supirRequest = RegisterRequest.builder()
                .username("Supir Sawit")
                .name("Budi")
                .email("budi@gmail.com")
                .password("supir123")
                .role(Role.SUPIR)
                .build();

        adminResponse = AuthResponse.builder()
                .username("Admin Sawit")
                .name("Agus")
                .email("admin@gmail.com")
                .role(Role.ADMIN)
                .build();

        mandorResponse = AuthResponse.builder()
                .username("Mandor Sawit")
                .name("Burhan")
                .email("burhan@gmail.com")
                .role(Role.MANDOR)
                .nomorSertifMandor("CERT-001")
                .build();

        buruhResponse = AuthResponse.builder()
                .username("Buruh Sawit")
                .name("Usep")
                .email("usep@gmail.com")
                .role(Role.BURUH)
                .build();

        supirResponse = AuthResponse.builder()
                .username("Supir Sawit")
                .name("Budi")
                .email("budi@gmail.com")
                .role(Role.SUPIR)
                .build();
    }

    @Test
    void registerAdminSuccess() {
        when(authRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(responseMapper.toResponse(any(User.class), any(), any())).thenReturn(adminResponse);

        final AuthResponse response = authService.register(adminRequest);

        assertEquals("Admin Sawit", response.getUsername());
        assertEquals("Agus", response.getName());
        assertEquals("admin@gmail.com", response.getEmail());
        assertEquals(Role.ADMIN, response.getRole());

        verify(registrationValidator).validateRequiredFields(adminRequest.getUsername(), adminRequest.getName(), adminRequest.getEmail(), adminRequest.getRole());
        verify(responseMapper).toResponse(any(User.class), isNull(), isNull());
    }

    @Test
    void registerMandorSuccess() {
        when(authRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(responseMapper.toResponse(any(User.class), any(), any())).thenReturn(mandorResponse);

        final AuthResponse response = authService.register(mandorRequest);

        assertEquals("Mandor Sawit", response.getUsername());
        assertEquals("Burhan", response.getName());
        assertEquals("burhan@gmail.com", response.getEmail());
        assertEquals(Role.MANDOR, response.getRole());
        assertEquals("CERT-001", response.getNomorSertifMandor());

        verify(registrationValidator).validateRequiredFields(mandorRequest.getUsername(), mandorRequest.getName(), mandorRequest.getEmail(), mandorRequest.getRole());
        verify(responseMapper).toResponse(any(User.class), isNull(), isNull());
    }

    @Test
    void registerBuruhSuccess() {
        when(authRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(responseMapper.toResponse(any(User.class), any(), any())).thenReturn(buruhResponse);

        final AuthResponse response = authService.register(buruhRequest);

        assertEquals("Buruh Sawit", response.getUsername());
        assertEquals("Usep", response.getName());
        assertEquals("usep@gmail.com", response.getEmail());
        assertEquals(Role.BURUH, response.getRole());

        verify(registrationValidator).validateRequiredFields(buruhRequest.getUsername(), buruhRequest.getName(), buruhRequest.getEmail(), buruhRequest.getRole());
        verify(responseMapper).toResponse(any(User.class), isNull(), isNull());
    }

    @Test
    void registerSupirSuccess() {
        when(authRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(responseMapper.toResponse(any(User.class), any(), any())).thenReturn(supirResponse);

        final AuthResponse response = authService.register(supirRequest);

        assertEquals("Supir Sawit", response.getUsername());
        assertEquals("Budi", response.getName());
        assertEquals("budi@gmail.com", response.getEmail());
        assertEquals(Role.SUPIR, response.getRole());

        verify(registrationValidator).validateRequiredFields(supirRequest.getUsername(), supirRequest.getName(), supirRequest.getEmail(), supirRequest.getRole());
        verify(responseMapper).toResponse(any(User.class), isNull(), isNull());
    }

    @Test
    void loginSuccess() {
        final AuthResponse loginResponse = AuthResponse.builder()
                .token("dummy.jwt.token")
                .refreshToken("dummy.refresh.token")
                .username("Admin Sawit")
                .name("Agus")
                .email("admin@gmail.com")
                .role(Role.ADMIN)
                .build();

        final AuthRequest request = AuthRequest.builder()
                .email("admin@gmail.com")
                .password("admin123")
                .build();

        doReturn(passwordStrategy).when(strategyFactory).resolve(AuthProvider.PASSWORD);
        when(passwordStrategy.authenticate(any(AuthRequest.class))).thenReturn(loginResponse);

        final AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("dummy.jwt.token", response.getToken());
        assertEquals("Admin Sawit", response.getUsername());
        assertEquals("Agus", response.getName());
        assertEquals("admin@gmail.com", response.getEmail());
        assertEquals(Role.ADMIN, response.getRole());

        verify(strategyFactory).resolve(AuthProvider.PASSWORD);
        verify(passwordStrategy).authenticate(any(AuthRequest.class));
    }

    @Test
    void logoutSuccess() {
        final String token = "valid.jwt.token";
        when(jwtUtil.extractUserId(token)).thenReturn("eb558e9f-1c39-460e-8860-71af6af63bd6");

        assertDoesNotThrow(() -> authService.logout(token));

        verify(tokenBlacklist, times(1)).blacklist(token);
        verify(responseMapper, never()).toResponse(any(), any(), any());
    }

    @Test
    void logoutNullToken() {
        assertThrows(InvalidCredentialException.class, () -> authService.logout(null));

        verify(tokenBlacklist, never()).blacklist(any());
        verify(responseMapper, never()).toResponse(any(), any(), any());
    }

    @Test
    void logoutBlankToken() {
        assertThrows(InvalidCredentialException.class, () -> authService.logout("   "));

        verify(tokenBlacklist, never()).blacklist(any());
        verify(responseMapper, never()).toResponse(any(), any(), any());
    }

    @Test
    void logoutExpiredOrInvalidTokenThrows() {
        final String badToken = "expired.or.malformed.token";
        doThrow(new RuntimeException("JWT expired")).when(jwtUtil).extractUserId(badToken);

        assertThrows(InvalidCredentialException.class, () -> authService.logout(badToken));

        verify(tokenBlacklist, never()).blacklist(any());
        verify(responseMapper, never()).toResponse(any(), any(), any());
    }

    @Test
    void getMeWithBlacklistedTokenThrows() {
        final String token = "blacklisted.jwt.token";
        when(tokenBlacklist.isBlacklisted(token)).thenReturn(true);

        assertThrows(InvalidCredentialException.class, () -> authService.getLoggedInUser(token));

        verify(authRepository, never()).findById(any());
        verify(jwtUtil, never()).generateToken(any(), any());
        verify(responseMapper, never()).toResponse(any(), any(), any());
    }

    @Test
    void refreshSuccess() {
        final String rawToken = "old.refresh.token";
        final String newAccessToken = "new.jwt.token";
        final String newRefreshTokenValue = "new.refresh.token";
        final UUID userId = UUID.randomUUID();

        adminUser.setId(userId);

        final RefreshToken oldRefreshToken = RefreshToken.builder()
                .userId(userId)
                .token(rawToken)
                .build();

        final RefreshToken newRefreshToken = RefreshToken.builder()
                .userId(userId)
                .token(newRefreshTokenValue)
                .build();

        final AuthResponse expectedResponse = AuthResponse.builder()
                .token(newAccessToken)
                .refreshToken(newRefreshTokenValue)
                .username("Admin Sawit")
                .role(Role.ADMIN)
                .build();

        when(refreshTokenService.validateAndGet(rawToken)).thenReturn(oldRefreshToken);
        when(authRepository.findById(userId)).thenReturn(adminUser);
        when(refreshTokenService.createRefreshToken(adminUser)).thenReturn(newRefreshToken);
        when(jwtUtil.generateToken(adminUser.getId(), adminUser.getRole())).thenReturn(newAccessToken);

        when(responseMapper.toResponse(any(User.class), anyString(), anyString())).thenReturn(expectedResponse);

        AuthResponse response = authService.refresh(rawToken);

        assertNotNull(response);
        assertEquals(newAccessToken, response.getToken());
        assertEquals(newRefreshTokenValue, response.getRefreshToken());

        verify(refreshTokenService).validateAndGet(rawToken);
        verify(authRepository).findById(userId);
        verify(refreshTokenRepository, never()).deleteByUserId(any());
        verify(refreshTokenService).createRefreshToken(adminUser);
        verify(jwtUtil).generateToken(adminUser.getId(), adminUser.getRole());
        verify(responseMapper).toResponse(adminUser, newAccessToken, newRefreshTokenValue);
    }

    @Test
    void refreshInvalidTokenThrows() {
        final String rawToken = "invalid.or.expired.token";
        when(refreshTokenService.validateAndGet(rawToken)).thenThrow(new InvalidCredentialException());

        assertThrows(InvalidCredentialException.class, () -> authService.refresh(rawToken));

        verify(refreshTokenService).validateAndGet(rawToken);
        verify(authRepository, never()).findById(any());
        verify(refreshTokenRepository, never()).deleteByUserId(any());
        verify(refreshTokenService, never()).createRefreshToken(any());
        verify(jwtUtil, never()).generateToken(any(), any());
        verify(responseMapper, never()).toResponse(any(), any(), any());
    }
}