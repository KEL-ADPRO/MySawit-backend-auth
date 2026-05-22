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
    void save_ShouldMergeAndReturnUser() {
        when(entityManager.merge(admin)).thenReturn(admin);

        final User result = authRepository.save(admin);

        assertEquals(admin, result);
        verify(entityManager).merge(admin);
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
        UUID id = admin.getId();
        when(entityManager.createQuery(any(String.class), eq(User.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("userId", id)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(admin));

        final User result = authRepository.findById(id);

        assertEquals(admin, result);
    }

    @Test
    void findById_UnknownId() {
        UUID id = UUID.randomUUID();
        when(entityManager.createQuery(any(String.class), eq(User.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("userId", id)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of());

        final User result = authRepository.findById(id);

        assertNull(result);
    }

    @Test
    void findByGoogleId_ExistingId() {
        when(entityManager.createQuery(any(String.class), eq(User.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("googleId", "google-id")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(admin));

        final User result = authRepository.findByGoogleId("google-id");

        assertEquals(admin, result);
    }

    @Test
    void findByGoogleId_UnknownId() {
        when(entityManager.createQuery(any(String.class), eq(User.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("googleId", "google-id")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of());

        final User result = authRepository.findByGoogleId("google-id");

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

    @Test
    void findByName_ShouldReturnMatchingUsers() {
        when(entityManager.createQuery(any(String.class), eq(User.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("name", "%Agus%")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(admin));

        List<User> result = authRepository.findByName("Agus");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(admin, result.getFirst());
        verify(entityManager).createQuery(any(String.class), eq(User.class));
    }

    @Test
    void findByName_NoMatch_ShouldReturnEmptyList() {
        when(entityManager.createQuery(any(String.class), eq(User.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("name", "%Unknown%")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of());

        List<User> result = authRepository.findByName("Unknown");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findByRole_ShouldReturnUsersWithRole() {
        when(entityManager.createQuery(any(String.class), eq(User.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("role", Role.ADMIN)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(admin));

        List<User> result = authRepository.findByRole(Role.ADMIN);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(admin, result.getFirst());
    }

    @Test
    void findByRole_NoMatch_ShouldReturnEmptyList() {
        when(entityManager.createQuery(any(String.class), eq(User.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("role", Role.MANDOR)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of());

        List<User> result = authRepository.findByRole(Role.MANDOR);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void delete_UserExists_ShouldRemoveUser() {
        UUID userId = admin.getId();

        when(entityManager.createQuery(any(String.class), eq(User.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("userId", userId)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(admin));

        authRepository.delete(userId);

        verify(entityManager, times(1)).remove(admin);
    }

    @Test
    void delete_UserDoesNotExist_ShouldNotRemoveUser() {
        UUID userId = UUID.randomUUID();

        when(entityManager.createQuery(any(String.class), eq(User.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("userId", userId)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of());

        authRepository.delete(userId);

        verify(entityManager, never()).remove(any(User.class));
    }
}