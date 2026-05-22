package com.mysawit.mysawit_auth.util;

import com.mysawit.mysawit_auth.model.Role;
import com.mysawit.mysawit_auth.model.User;
import com.mysawit.mysawit_auth.repository.AuthRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DataSeederTest {

    @Mock
    private AuthRepository authRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @InjectMocks
    private DataSeeder dataSeeder;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(dataSeeder, "adminEmail", "admin@test.com");
        ReflectionTestUtils.setField(dataSeeder, "adminPassword", "testPassword");
        ReflectionTestUtils.setField(dataSeeder, "adminName", "Test Admin");
        ReflectionTestUtils.setField(dataSeeder, "adminUsername", "Test Username");
    }

    @Test
    void createsAdminWhenNotExists() {
        when(authRepository.findByEmail("admin@test.com")).thenReturn(Optional.empty());
        when(passwordHasher.hash("testPassword")).thenReturn("hashed_testPassword");

        dataSeeder.run();

        final ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(authRepository, times(1)).save(captor.capture());

        final User saved = captor.getValue();
        assertEquals("admin@test.com", saved.getEmail());
        assertEquals("hashed_testPassword", saved.getPassword());
        assertEquals("Test Admin", saved.getName());
        assertEquals("Test Username", saved.getUsername());
        assertEquals(Role.ADMIN, saved.getRole());
    }

    @Test
    void skipsWhenAdminAlreadyExists() {
        when(authRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(new User()));

        dataSeeder.run();

        verify(authRepository, never()).save(any());
        verify(passwordHasher, never()).hash(any());
    }

    @Test
    void hashesPasswordBeforeSaving() {
        when(authRepository.findByEmail("admin@test.com")).thenReturn(Optional.empty());
        when(passwordHasher.hash("testPassword")).thenReturn("hashed_testPassword");

        dataSeeder.run();

        verify(passwordHasher, times(1)).hash("testPassword");

        final ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(authRepository).save(captor.capture());
        assertNotEquals("testPassword", captor.getValue().getPassword());
    }

    @Test
    void savedUserIsAdmin() {
        when(authRepository.findByEmail("admin@test.com")).thenReturn(Optional.empty());
        when(passwordHasher.hash(any())).thenReturn("hashed");

        dataSeeder.run();

        final ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(authRepository).save(captor.capture());
        assertEquals(Role.ADMIN, captor.getValue().getRole());
    }

    @Test
    void multipleRunsDoesntDuplicate() {
        when(authRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(new User()));

        dataSeeder.run();
        dataSeeder.run();
        dataSeeder.run();

        verify(authRepository, never()).save(any());
    }
}