package com.mysawit.mysawit_auth.service;

import com.mysawit.mysawit_auth.exception.InvalidCredentialException;
import com.mysawit.mysawit_auth.mapper.AuthResponseMapper;
import com.mysawit.mysawit_auth.model.RefreshToken;
import com.mysawit.mysawit_auth.model.Role;
import com.mysawit.mysawit_auth.model.User;
import com.mysawit.mysawit_auth.repository.AuthRepository;
import com.mysawit.mysawit_auth.util.*;
import com.mysawit.mysawit_auth.dto.request.LoginRequest;
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
    private LoginAttemptService loginAttemptService;

    @Mock
    private RefreshTokenService refreshTokenService;

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

        final RefreshToken refreshToken = RefreshToken.builder().token("dummy.refresh.token").build();

        when(authRepository.findByEmail("admin@gmail.com")).thenReturn(adminUser);
        when(passwordHasher.matches("admin123", "hashed_admin123")).thenReturn(true);
        when(jwtUtil.generateToken(adminUser.getId(), adminUser.getRole())).thenReturn("dummy.jwt.token");
        when(refreshTokenService.createRefreshToken(adminUser)).thenReturn(refreshToken);
        when(responseMapper.toResponse(any(User.class), any(), any())).thenReturn(loginResponse);

        final LoginRequest request = LoginRequest.builder()
                .email("admin@gmail.com")
                .password("admin123")
                .build();

        AuthResponse response = authService.login(request);

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
    void loginNullRequest() {
        assertThrows(InvalidCredentialException.class, () -> authService.login(null));
    }

    @Test
    void loginUnknownEmail() {
        when(authRepository.findByEmail("unknownUser@gmail.com")).thenReturn(null);

        final LoginRequest request = LoginRequest.builder()
                .email("unknownUser@gmail.com")
                .password("unknownPassword")
                .build();

        assertThrows(InvalidCredentialException.class, () -> authService.login(request));

        verify(jwtUtil, never()).generateToken(any(), any());
        verify(responseMapper, never()).toResponse(any(), any(), any());
        verify(loginAttemptService).recordFailure("unknownUser@gmail.com");
        verify(loginAttemptService, never()).recordSuccess(any());
    }

    @Test
    void loginBlankEmail() {
        final LoginRequest request = LoginRequest.builder()
                .email("  ")
                .password("admin123")
                .build();

        assertThrows(InvalidCredentialException.class, () -> authService.login(request));

        verify(jwtUtil, never()).generateToken(any(), any());
        verify(responseMapper, never()).toResponse(any(), any(), any());
    }

    @Test
    void loginNullEmail() {
        final LoginRequest request = LoginRequest.builder()
                .email(null)
                .password("admin123")
                .build();

        assertThrows(InvalidCredentialException.class, () -> authService.login(request));

        verify(jwtUtil, never()).generateToken(any(), any());
        verify(responseMapper, never()).toResponse(any(), any(), any());
    }

    @Test
    void loginBlankPassword() {
        final LoginRequest request = LoginRequest.builder()
                .email("admin@gmail.com")
                .password("")
                .build();

        assertThrows(InvalidCredentialException.class, () -> authService.login(request));

        verify(jwtUtil, never()).generateToken(any(), any());
        verify(responseMapper, never()).toResponse(any(), any(), any());
    }

    @Test
    void loginNullPassword() {
        final LoginRequest request = LoginRequest.builder()
                .email("admin@gmail.com")
                .password(null)
                .build();

        assertThrows(InvalidCredentialException.class, () -> authService.login(request));

        verify(jwtUtil, never()).generateToken(any(), any());
        verify(responseMapper, never()).toResponse(any(), any(), any());
    }

    @Test
    void loginWrongPassword() {
        when(authRepository.findByEmail("admin@gmail.com")).thenReturn(adminUser);
        when(passwordHasher.matches("wrongPassword", "hashed_admin123")).thenReturn(false);

        final LoginRequest request = LoginRequest.builder()
                .email("admin@gmail.com")
                .password("wrongPassword")
                .build();

        assertThrows(InvalidCredentialException.class, () -> authService.login(request));

        verify(jwtUtil, never()).generateToken(any(), any());
        verify(responseMapper, never()).toResponse(any(), any(), any());
        verify(loginAttemptService).recordFailure("admin@gmail.com");
        verify(loginAttemptService, never()).recordSuccess(any());
    }

    @Test
    void loginWithGoogleOnlyAccount() {
        final User googleOnlyUser = User.builder()
                .email("google@gmail.com")
                .password(null)
                .googleId("google-id-12345")
                .role(Role.BURUH)
                .username("google@gmail.com")
                .name("Google User")
                .build();

        when(authRepository.findByEmail("google@gmail.com")).thenReturn(googleOnlyUser);

        final LoginRequest request = LoginRequest.builder()
                .email("google@gmail.com")
                .password("somepassword")
                .build();

        assertThrows(IllegalArgumentException.class, () -> authService.login(request));

        verify(jwtUtil, never()).generateToken(any(), any());
        verify(responseMapper, never()).toResponse(any(), any(), any());
    }

    @Test
    void loginWithNoGoogleIdAndNoPasswordThrows() {
        final User corruptedUser = User.builder()
                .email("broken@gmail.com")
                .googleId(null)
                .password(null)
                .role(Role.BURUH)
                .username("broken@gmail.com")
                .name("Broken User")
                .build();

        when(authRepository.findByEmail("broken@gmail.com")).thenReturn(corruptedUser);

        final LoginRequest request = LoginRequest.builder()
                .email("broken@gmail.com")
                .password("anything")
                .build();

        assertThrows(InvalidCredentialException.class, () -> authService.login(request));

        verify(jwtUtil, never()).generateToken(any(), any());
        verify(responseMapper, never()).toResponse(any(), any(), any());
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
        verify(refreshTokenService).revokeAll(adminUser);
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
        verify(refreshTokenService, never()).revokeAll(any());
        verify(refreshTokenService, never()).createRefreshToken(any());
        verify(jwtUtil, never()).generateToken(any(), any());
        verify(responseMapper, never()).toResponse(any(), any(), any());
    }
}