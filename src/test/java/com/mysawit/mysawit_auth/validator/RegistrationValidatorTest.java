package com.mysawit.mysawit_auth.validator;

import com.mysawit.mysawit_auth.exception.EmailAlreadyExistsException;
import com.mysawit.mysawit_auth.exception.InvalidCredentialException;
import com.mysawit.mysawit_auth.exception.MandorSertifMissingException;
import com.mysawit.mysawit_auth.model.Role;
import com.mysawit.mysawit_auth.model.User;
import com.mysawit.mysawit_auth.repository.AuthRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RegistrationValidatorTest {

    @Mock
    private AuthRepository authRepository;

    @InjectMocks
    private RegistrationValidator validator;

    @Test
    void validateRequiredFieldsSuccess() {
        assertDoesNotThrow(() -> validator.validateRequiredFields("Admin Sawit", "Agus", "admin@gmail.com", Role.ADMIN));
        assertDoesNotThrow(() -> validator.validateRequiredFields("Buruh Sawit", "Usep", "usep@gmail.com", Role.BURUH));
    }

    @Test
    void validateRequiredFieldsBlank() {
        assertThrows(InvalidCredentialException.class, () ->
                validator.validateRequiredFields("", "Budi", "budi@gmail.com", Role.SUPIR));
    }

    @Test
    void validateRequiredFieldsNullRole() {
        assertThrows(InvalidCredentialException.class, () ->
                validator.validateRequiredFields("Admin Sawit", "Agus", "admin@gmail.com", null));
    }

    @Test
    void validatePasswordSuccess() {
        assertDoesNotThrow(() -> validator.validatePassword("mandor123"));
        assertDoesNotThrow(() -> validator.validatePassword("supir123"));
    }

    @Test
    void validatePasswordBlank() {
        assertThrows(InvalidCredentialException.class, () -> validator.validatePassword(""));
    }

    @Test
    void validatePasswordNull() {
        assertThrows(InvalidCredentialException.class, () -> validator.validatePassword(null));
    }

    @Test
    void assertEmailUniqueSuccess() {
        when(authRepository.findByEmail("usep@gmail.com")).thenReturn(null);

        assertDoesNotThrow(() -> validator.assertEmailUnique("usep@gmail.com"));
    }

    @Test
    void assertEmailUniqueDuplicate() {
        final User existingUser = new User();
        when(authRepository.findByEmail("admin@gmail.com")).thenReturn(existingUser);

        assertThrows(EmailAlreadyExistsException.class, () -> validator.assertEmailUnique("admin@gmail.com"));
    }

    @Test
    void assertMandorCertPresentSuccess() {
        assertDoesNotThrow(() -> validator.assertMandorCertPresent(Role.MANDOR, "CERT-001"));
    }

    @Test
    void assertMandorCertPresentNotMandor() {
        assertDoesNotThrow(() -> validator.assertMandorCertPresent(Role.BURUH, ""));
        assertDoesNotThrow(() -> validator.assertMandorCertPresent(Role.SUPIR, null));
    }

    @Test
    void assertMandorCertPresentBlank() {
        assertThrows(MandorSertifMissingException.class, () -> validator.assertMandorCertPresent(Role.MANDOR, ""));
    }

    @Test
    void assertMandorCertPresentNull() {
        assertThrows(MandorSertifMissingException.class, () -> validator.assertMandorCertPresent(Role.MANDOR, null));
    }
}