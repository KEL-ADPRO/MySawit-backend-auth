package com.mysawit.mysawit_auth.service;

import com.mysawit.mysawit_auth.exception.EmailAlreadyExistsException;
import com.mysawit.mysawit_auth.exception.MandorSertifMissingException;
import com.mysawit.mysawit_auth.model.Role;
import com.mysawit.mysawit_auth.model.User;
import com.mysawit.mysawit_auth.repository.AuthRepository;
import com.mysawit.mysawit_auth.util.AuthResponse;
import com.mysawit.mysawit_auth.util.PasswordHasher;
import com.mysawit.mysawit_auth.util.RegisterRequest;
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
public class AuthServiceTest {
    @Mock
    private AuthRepository authRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest adminRequest;
    private RegisterRequest mandorRequest;
    private RegisterRequest buruhRequest;
    private RegisterRequest supirRequest;

    @BeforeEach
    void setUp() {
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

        AuthResponse response = authService.register(adminRequest);

        assertEquals("Admin Sawit", response.getUsername());
        assertEquals("Agus", response.getName());
        assertEquals("admin@gmail.com", response.getEmail());
        assertEquals(Role.ADMIN, response.getRole());
    }

    @Test
    void registerMandorSuccess() {
        when(authRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        AuthResponse response = authService.register(mandorRequest);

        assertEquals("Mandor Sawit", response.getUsername());
        assertEquals("Burhan", response.getName());
        assertEquals("burhan@gmail.com", response.getEmail());
        assertEquals(Role.MANDOR, response.getRole());
        assertEquals("CERT-001", response.getNomorSertifMandor());
    }

    @Test
    void registerBuruhSuccess() {
        when(authRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        AuthResponse response = authService.register(buruhRequest);

        assertEquals("Buruh Sawit", response.getUsername());
        assertEquals("Usep", response.getName());
        assertEquals("usep@gmail.com", response.getEmail());
        assertEquals(Role.BURUH, response.getRole());
    }

    @Test
    void registerSupirSuccess() {
        when(authRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

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
}