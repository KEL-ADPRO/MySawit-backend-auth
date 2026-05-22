package com.mysawit.mysawit_auth.validator;

import com.mysawit.mysawit_auth.model.Role;
import com.mysawit.mysawit_auth.model.User;
import com.mysawit.mysawit_auth.repository.AuthRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdminValidatorTest {

    @Mock
    private AuthRepository authRepository;

    @InjectMocks
    private AdminValidator adminValidator;

    private UUID validId;
    private User buruhUser;
    private User mandorUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        validId = UUID.randomUUID();

        buruhUser = User.builder()
                .id(validId)
                .role(Role.BURUH)
                .build();

        mandorUser = User.builder()
                .id(validId)
                .role(Role.MANDOR)
                .build();

        adminUser = User.builder()
                .id(validId)
                .role(Role.ADMIN)
                .build();
    }

    @Test
    void requireBuruhSuccess() {
        when(authRepository.findById(validId)).thenReturn(buruhUser);

        final User result = adminValidator.requireBuruh(validId);

        assertNotNull(result);
        assertEquals(Role.BURUH, result.getRole());
    }

    @Test
    void requireBuruhNotFound() {
        when(authRepository.findById(validId)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> adminValidator.requireBuruh(validId));
    }

    @Test
    void requireBuruhWrongRole() {
        when(authRepository.findById(validId)).thenReturn(adminUser);

        assertThrows(IllegalArgumentException.class, () -> adminValidator.requireBuruh(validId));
    }

    @Test
    void requireMandorSuccess() {
        when(authRepository.findById(validId)).thenReturn(mandorUser);

        final User result = adminValidator.requireMandor(validId);

        assertNotNull(result);
        assertEquals(Role.MANDOR, result.getRole());
    }

    @Test
    void requireMandorNotFound() {
        when(authRepository.findById(validId)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> adminValidator.requireMandor(validId));
    }

    @Test
    void requireMandorWrongRole() {
        when(authRepository.findById(validId)).thenReturn(buruhUser);

        assertThrows(IllegalArgumentException.class, () -> adminValidator.requireMandor(validId));
    }
}
