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
    private User buruh;
    private final UUID mandorId = UUID.fromString("fc558e9f-1c39-460e-8860-71af6af63bd6");

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

        buruh = User.builder()
                .id(UUID.randomUUID())
                .username("Buruh Sawit")
                .name("Candra")
                .email("buruh@gmail.com")
                .password("buruh123")
                .role(Role.BURUH)
                .build();

    }

    @Test
    void saveSuccess() {
        when(entityManager.merge(admin)).thenReturn(admin);

        final User result = authRepository.save(admin);

        assertEquals(admin, result);
        verify(entityManager).merge(admin);
    }

    @Test
    void findByEmailExistingEmail() {
        when(entityManager.createQuery(any(String.class), eq(User.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("email", "admin@gmail.com")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(admin));

        final User result = authRepository.findByEmail("admin@gmail.com");

        assertEquals(admin, result);
    }

    @Test
    void findByEmailUnknownEmail() {
        when(entityManager.createQuery(any(String.class), eq(User.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("email", "unknown@gmail.com")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of());

        final User result = authRepository.findByEmail("unknown@gmail.com");

        assertNull(result);
    }

    @Test
    void findByIdExistingId() {
        UUID id = admin.getId();
        when(entityManager.createQuery(any(String.class), eq(User.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("userId", id)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(admin));

        final User result = authRepository.findById(id);

        assertEquals(admin, result);
    }

    @Test
    void findByIdUnknownId() {
        UUID id = UUID.randomUUID();
        when(entityManager.createQuery(any(String.class), eq(User.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("userId", id)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of());

        final User result = authRepository.findById(id);

        assertNull(result);
    }

    @Test
    void findByGoogleIdExistingId() {
        when(entityManager.createQuery(any(String.class), eq(User.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("googleId", "google-id")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(admin));

        final User result = authRepository.findByGoogleId("google-id");

        assertEquals(admin, result);
    }

    @Test
    void findByGoogleIdUnknownId() {
        when(entityManager.createQuery(any(String.class), eq(User.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("googleId", "google-id")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of());

        final User result = authRepository.findByGoogleId("google-id");

        assertNull(result);
    }

    @Test
    void findByUsernameExistingUsername() {
        when(entityManager.createQuery(any(String.class), eq(User.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("username", "Buruh Sawit")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(buruh));

        final User result = authRepository.findByUsername("Buruh Sawit");

        assertEquals(buruh, result);
    }

    @Test
    void findByUsernameUnknownUsername() {
        when(entityManager.createQuery(any(String.class), eq(User.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("username", "unknown")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of());

        final User result = authRepository.findByUsername("unknown");

        assertNull(result);
    }

    @Test
    void findByNameMatch() {
        when(entityManager.createQuery(any(String.class), eq(User.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("name", "%Agus%")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(admin));

        final List<User> result = authRepository.findByName("Agus");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(admin, result.getFirst());
        verify(entityManager).createQuery(any(String.class), eq(User.class));
    }

    @Test
    void findByNameNoMatch() {
        when(entityManager.createQuery(any(String.class), eq(User.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("name", "%Unknown%")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of());

        final List<User> result = authRepository.findByName("Unknown");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findByRoleMatch() {
        when(entityManager.createQuery(any(String.class), eq(User.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("role", Role.ADMIN)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(admin));

        final List<User> result = authRepository.findByRole(Role.ADMIN);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(admin, result.getFirst());
    }

    @Test
    void findByRoleNoMatch() {
        when(entityManager.createQuery(any(String.class), eq(User.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("role", Role.MANDOR)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of());

        final List<User> result = authRepository.findByRole(Role.MANDOR);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findByMandorIdExistingMandorId() {
        when(entityManager.createQuery(any(String.class), eq(User.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("mandorid", mandorId)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(buruh));

        List<User> result = authRepository.findByMandorId(mandorId);

        assertEquals(1, result.size());
        assertEquals(buruh, result.getFirst());
        verify(typedQuery).setParameter("mandorid", mandorId);
    }

    @Test
    void deleteUserExists() {
        final UUID userId = admin.getId();

        when(entityManager.createQuery(any(String.class), eq(User.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("userId", userId)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(admin));

        authRepository.delete(userId);

        verify(entityManager, times(1)).remove(admin);
    }

    @Test
    void deleteUserDoesNotExist() {
        final UUID userId = UUID.randomUUID();

        when(entityManager.createQuery(any(String.class), eq(User.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("userId", userId)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of());

        authRepository.delete(userId);

        verify(entityManager, never()).remove(any(User.class));
    }
}