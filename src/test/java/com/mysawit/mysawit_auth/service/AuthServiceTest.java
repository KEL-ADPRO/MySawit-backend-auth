package com.mysawit.mysawit_auth.service;

import com.mysawit.mysawit_auth.exception.EmailAlreadyExistsException;
import com.mysawit.mysawit_auth.exception.MandorSertifMissingException;
import com.mysawit.mysawit_auth.model.Role;
import com.mysawit.mysawit_auth.model.User;
import com.mysawit.mysawit_auth.repository.AuthRepository;
import com.mysawit.mysawit_auth.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AuthServiceTest {
    @Mock
    private AuthRepository authRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private JwtUtil jwtUtil;

    private RegisterRequest adminRequest;
    private RegisterRequest mandorRequest;
    private RegisterRequest buruhRequest;
    private RegisterRequest supirRequest;

    private User adminUser;

    @BeforeEach
    void setUp() {
        when(authRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

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
    }

    @Test
    void registerAdminSuccess() {
        AuthResponse response = authService.register(adminRequest);

        assertEquals("Admin Sawit", response.getUsername());
        assertEquals("Agus", response.getName());
        assertEquals("admin@gmail.com", response.getEmail());
        assertEquals(Role.ADMIN, response.getRole());
    }

    @Test
    void registerMandorSuccess() {
        AuthResponse response = authService.register(mandorRequest);

        assertEquals("Mandor Sawit", response.getUsername());
        assertEquals("Burhan", response.getName());
        assertEquals("burhan@gmail.com", response.getEmail());
        assertEquals(Role.MANDOR, response.getRole());
        assertEquals("CERT-001", response.getNomorSertifMandor());
    }

    @Test
    void registerBuruhSuccess() {
        AuthResponse response = authService.register(buruhRequest);

        assertEquals("Buruh Sawit", response.getUsername());
        assertEquals("Usep", response.getName());
        assertEquals("usep@gmail.com", response.getEmail());
        assertEquals(Role.BURUH, response.getRole());
    }

    @Test
    void registerSupirSuccess() {
        AuthResponse response = authService.register(supirRequest);

        assertEquals("Supir Sawit", response.getUsername());
        assertEquals("Budi", response.getName());
        assertEquals("budi@gmail.com", response.getEmail());
        assertEquals(Role.SUPIR, response.getRole());
    }

    @Test
    void registerNull() {
        assertThrows(IllegalArgumentException.class, () -> authService.register(null));
    }

    @Test
    void registerNoUsername() {
        RegisterRequest request = adminRequest.toBuilder()
                .username("")
                .build();

        assertThrows(IllegalArgumentException.class, () -> authService.register(request));
    }

    @Test
    void registerNoName() {
        RegisterRequest request = adminRequest.toBuilder()
                .name("")
                .build();

        assertThrows(IllegalArgumentException.class, () -> authService.register(request));
    }

    @Test
    void registerNoEmail() {
        RegisterRequest request = adminRequest.toBuilder()
                .email(null)
                .build();

        assertThrows(IllegalArgumentException.class, () -> authService.register(request));
    }

    @Test
    void registerNoPassword() {
        when(passwordHasher.hash(any())).thenReturn("hashed_password");

        RegisterRequest request = adminRequest.toBuilder()
                .password(null)
                .build();

        assertThrows(IllegalArgumentException.class, () -> authService.register(request));
    }

    @Test
    void registerNoRole() {
        RegisterRequest request = adminRequest.toBuilder()
                .role(null)
                .build();

        assertThrows(IllegalArgumentException.class, () -> authService.register(request));
    }

    @Test
    void registerMandorNoSertif() {
        RegisterRequest request = mandorRequest.toBuilder()
                .nomorSertifMandor(null)
                .build();

        assertThrows(MandorSertifMissingException.class, () -> authService.register(request));
    }

    @Test
    void registerDuplicateEmail() {
        when(authRepository.findByEmail("admin@gmail.com")).thenReturn(new User());

        RegisterRequest newRequest = RegisterRequest.builder()
                .username("Admin 2")
                .name("atmin")
                .email("admin@gmail.com")
                .password("atmin123")
                .role(Role.ADMIN)
                .build();

        assertThrows(EmailAlreadyExistsException.class, () -> authService.register(newRequest));
        verify(authRepository, never()).save(any(User.class));
    }

    @Test
    void loginSuccess() {
        when(authRepository.findByEmail("admin@gmail.com")).thenReturn(adminUser);
        when(passwordHasher.matches("admin123", "hashed_admin123")).thenReturn(true);
        when(jwtUtil.generateToken("admin@gmail.com")).thenReturn("dummy.jwt.token");

        LoginRequest request = LoginRequest.builder()
                .email("admin@gmail.com")
                .password("admin123")
                .build();

        AuthResponse response = authService.login(request);

        verify(authRepository, times(1)).findByEmail("admin@gmail.com");
        verify(jwtUtil, times(1)).generateToken("admin@gmail.com");
        assertNotNull(response);
        assertEquals("dummy.jwt.token", response.getToken());
        assertEquals("Admin Sawit", response.getUsername());
        assertEquals("Agus", response.getName());
        assertEquals("admin@gmail.com", response.getEmail());
        assertEquals(Role.ADMIN, response.getRole());
    }

    @Test
    void loginNullRequest() {
        assertThrows(IllegalArgumentException.class, () -> authService.login(null));
    }

    @Test
    void loginUnknownEmail() {
        when(authRepository.findByEmail("unknownUser@gmail.com")).thenReturn(null);

        LoginRequest request = LoginRequest.builder()
                .email("unknownUser@gmail.com")
                .password("unknownPassword")
                .build();

        assertThrows(IllegalArgumentException.class, () -> authService.login(request));
        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    void loginBlankEmail() {
        LoginRequest request = LoginRequest.builder()
                .email("  ")
                .password("admin123")
                .build();

        assertThrows(IllegalArgumentException.class, () -> authService.login(request));
    }

    @Test
    void loginNullEmail() {
        LoginRequest request = LoginRequest.builder()
                .email(null)
                .password("admin123")
                .build();

        assertThrows(IllegalArgumentException.class, () -> authService.login(request));
    }

    @Test
    void loginBlankPassword() {
        LoginRequest request = LoginRequest.builder()
                .email("admin@gmail.com")
                .password("")
                .build();

        assertThrows(IllegalArgumentException.class, () -> authService.login(request));
    }

    @Test
    void loginNullPassword() {
        LoginRequest request = LoginRequest.builder()
                .email("admin@gmail.com")
                .password(null)
                .build();

        assertThrows(IllegalArgumentException.class, () -> authService.login(request));
    }

    @Test
    void loginWrongPassword() {
        when(authRepository.findByEmail("admin@gmail.com")).thenReturn(adminUser);
        when(passwordHasher.matches("atmin456", "hashed_atmin456")).thenReturn(false);

        LoginRequest request = LoginRequest.builder()
                .email("admin@gmail.com")
                .password("wrongPassword")
                .build();

        assertThrows(IllegalArgumentException.class, () -> authService.login(request));
        verify(jwtUtil, never()).generateToken(any());
    }
}