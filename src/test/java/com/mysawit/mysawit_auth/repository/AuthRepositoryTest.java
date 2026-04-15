package com.mysawit.mysawit_auth.repository;

import com.mysawit.mysawit_auth.model.Role;
import com.mysawit.mysawit_auth.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthRepositoryTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<User> typedQuery;

    @InjectMocks
    private AuthRepositoryImpl authRepository;

    private User admin;
    private User mandor;
    private User buruh;
    private User supir;

    @BeforeEach
    void setUp() {
        admin = User.builder()
                .id(UUID.fromString("eb558e9f-1c39-460e-8860-71af6af63bd6"))
                .username("Admin Sawit")
                .name("Agus")
                .email("admin@gmail.com")
                .password("admin123")
                .role(Role.ADMIN)
                .build();

        mandor = User.builder()
                .id(UUID.fromString("fc558e9f-1c39-460e-8860-71af6af63bd6"))
                .username("Mandor Sawit")
                .name("Burhan")
                .email("burhan@gmail.com")
                .password("mandor123")
                .role(Role.MANDOR)
                .nomorSertifMandor("CERT-001")
                .build();

        buruh = User.builder()
                .id(UUID.fromString("ab558e9f-1c39-460e-8860-71af6af63bd6"))
                .username("Buruh Sawit")
                .name("Usep")
                .email("usep@gmail.com")
                .password("buruh123")
                .role(Role.BURUH)
                .build();

        supir = User.builder()
                .id(UUID.fromString("cd558e9f-1c39-460e-8860-71af6af63bd6"))
                .username("Supir Sawit")
                .name("Budi")
                .email("budi@gmail.com")
                .password("supir123")
                .role(Role.SUPIR)
                .build();
    }

    @Test
    void saveCheck() {
        when(entityManager.merge(admin)).thenReturn(admin);

        final User result = authRepository.save(admin);

        assertEquals(admin, result);
        verify(entityManager, times(1)).merge(admin);
    }

    @Test
    void saveMandorReturnsUser() {
        when(entityManager.merge(mandor)).thenReturn(mandor);

        final User result = authRepository.save(mandor);

        assertEquals(mandor, result);
    }

    @Test
    void saveBuruhReturnsUser() {
        when(entityManager.merge(buruh)).thenReturn(buruh);

        final User result = authRepository.save(buruh);

        assertEquals(buruh, result);
    }

    @Test
    void saveSupirReturnsUser() {
        when(entityManager.merge(supir)).thenReturn(supir);

        final User result = authRepository.save(supir);

        assertEquals(supir, result);
    }

    @Test
    void findByEmail_ExistingEmail() {
        when(entityManager.createQuery(any(String.class), eq(User.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("email", "admin@gmail.com")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(admin));

        final User result = authRepository.findByEmail("admin@gmail.com");

        assertEquals(admin, result);
    }

    @Test
    void findByEmail_UnknownEmail() {
        when(entityManager.createQuery(any(String.class), eq(User.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("email", "unknown@gmail.com")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of());

        final User result = authRepository.findByEmail("unknown@gmail.com");

        assertNull(result);
    }

    @Test
    void findById_ExistingId() {
        when(entityManager.createQuery(any(String.class), eq(User.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("userId", "eb558e9f-1c39-460e-8860-71af6af63bd6")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(admin));

        final User result = authRepository.findById(UUID.fromString("eb558e9f-1c39-460e-8860-71af6af63bd6"));

        assertEquals(admin, result);
    }

    @Test
    void findById_UnknownId() {
        when(entityManager.createQuery(any(String.class), eq(User.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("userId", "eb558e9f-1c39-460e-8860-71af6af63bd6")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of());

        final User result = authRepository.findById(UUID.fromString("eb558e9f-1c39-460e-8860-71af6af63bd6"));

        assertNull(result);
    }

    @Test
    void findByUsername_ExistingUsername() {
        when(entityManager.createQuery(any(String.class), eq(User.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("username", "Buruh Sawit")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(buruh));

        final User result = authRepository.findByUsername("Buruh Sawit");

        assertEquals(buruh, result);
    }

    @Test
    void findByUsername_UnknownUsername() {
        when(entityManager.createQuery(any(String.class), eq(User.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("username", "unknown")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of());

        final User result = authRepository.findByUsername("unknown");

        assertNull(result);
    }
}