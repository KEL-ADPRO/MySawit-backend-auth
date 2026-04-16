package com.mysawit.mysawit_auth.service;

import com.mysawit.mysawit_auth.exception.EmailAlreadyExistsException;
import com.mysawit.mysawit_auth.exception.InvalidCredentialException;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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
    }

    @Test
    void registerAdminSuccess() {
        when(authRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        final AuthResponse response = authService.register(adminRequest);

        assertEquals("Admin Sawit", response.getUsername());
        assertEquals("Agus", response.getName());
        assertEquals("admin@gmail.com", response.getEmail());
        assertEquals(Role.ADMIN, response.getRole());
    }

    @Test
    void registerMandorSuccess() {
        when(authRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        final AuthResponse response = authService.register(mandorRequest);

        assertEquals("Mandor Sawit", response.getUsername());
        assertEquals("Burhan", response.getName());
        assertEquals("burhan@gmail.com", response.getEmail());
        assertEquals(Role.MANDOR, response.getRole());
        assertEquals("CERT-001", response.getNomorSertifMandor());
    }

    @Test
    void registerBuruhSuccess() {
        when(authRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        final AuthResponse response = authService.register(buruhRequest);

        assertEquals("Buruh Sawit", response.getUsername());
        assertEquals("Usep", response.getName());
        assertEquals("usep@gmail.com", response.getEmail());
        assertEquals(Role.BURUH, response.getRole());
    }

    @Test
    void registerSupirSuccess() {
        when(authRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        final AuthResponse response = authService.register(supirRequest);

        assertEquals("Supir Sawit", response.getUsername());
        assertEquals("Budi", response.getName());
        assertEquals("budi@gmail.com", response.getEmail());
        assertEquals(Role.SUPIR, response.getRole());
    }

    @Test
    void registerNull() {
        assertThrows(InvalidCredentialException.class, () -> authService.register(null));
    }

    @Test
    void registerNoUsername() {
        final RegisterRequest request = adminRequest.toBuilder()
                .username("")
                .build();

        assertThrows(InvalidCredentialException.class, () -> authService.register(request));
    }

    @Test
    void registerNoName() {
        final RegisterRequest request = adminRequest.toBuilder()
                .name("")
                .build();

        assertThrows(InvalidCredentialException.class, () -> authService.register(request));
    }

    @Test
    void registerNoEmail() {
        final RegisterRequest request = adminRequest.toBuilder()
                .email(null)
                .build();

        assertThrows(InvalidCredentialException.class, () -> authService.register(request));
    }

    @Test
    void registerNoPassword() {
        RegisterRequest request = adminRequest.toBuilder()
                .password(null)
                .build();

        assertThrows(InvalidCredentialException.class, () -> authService.register(request));
    }

    @Test
    void registerNoRole() {
        final RegisterRequest request = adminRequest.toBuilder()
                .role(null)
                .build();

        assertThrows(InvalidCredentialException.class, () -> authService.register(request));
    }

    @Test
    void registerMandorNoSertif() {
        final RegisterRequest request = mandorRequest.toBuilder()
                .nomorSertifMandor(null)
                .build();

        assertThrows(MandorSertifMissingException.class, () -> authService.register(request));
    }

    @Test
    void registerDuplicateEmail() {
        when(authRepository.findByEmail("admin@gmail.com")).thenReturn(new User());

        final RegisterRequest newRequest = RegisterRequest.builder()
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
        when(jwtUtil.generateToken(adminUser.getId(), adminUser.getRole())).thenReturn("dummy.jwt.token");

        final LoginRequest request = LoginRequest.builder()
                .email("admin@gmail.com")
                .password("admin123")
                .build();

        AuthResponse response = authService.login(request);

        verify(authRepository, times(1)).findByEmail("admin@gmail.com");
        verify(jwtUtil, times(1)).generateToken(adminUser.getId(), adminUser.getRole());
        assertNotNull(response);
        assertEquals("dummy.jwt.token", response.getToken());
        assertEquals("Admin Sawit", response.getUsername());
        assertEquals("Agus", response.getName());
        assertEquals("admin@gmail.com", response.getEmail());
        assertEquals(Role.ADMIN, response.getRole());
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
    }

    @Test
    void loginBlankEmail() {
        final LoginRequest request = LoginRequest.builder()
                .email("  ")
                .password("admin123")
                .build();

        assertThrows(InvalidCredentialException.class, () -> authService.login(request));
    }

    @Test
    void loginNullEmail() {
        final LoginRequest request = LoginRequest.builder()
                .email(null)
                .password("admin123")
                .build();

        assertThrows(InvalidCredentialException.class, () -> authService.login(request));
    }

    @Test
    void loginBlankPassword() {
        final LoginRequest request = LoginRequest.builder()
                .email("admin@gmail.com")
                .password("")
                .build();

        assertThrows(InvalidCredentialException.class, () -> authService.login(request));
    }

    @Test
    void loginNullPassword() {
        final LoginRequest request = LoginRequest.builder()
                .email("admin@gmail.com")
                .password(null)
                .build();

        assertThrows(InvalidCredentialException.class, () -> authService.login(request));
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
    }
}