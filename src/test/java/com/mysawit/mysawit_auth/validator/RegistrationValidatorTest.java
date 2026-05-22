package com.mysawit.mysawit_auth.validator;

import com.mysawit.mysawit_auth.exception.EmailAlreadyExistsException;
import com.mysawit.mysawit_auth.exception.InvalidCredentialException;
import com.mysawit.mysawit_auth.exception.MandorSertifMissingException;
import com.mysawit.mysawit_auth.exception.WeakPasswordException;
import com.mysawit.mysawit_auth.model.Role;
import com.mysawit.mysawit_auth.model.User;
import com.mysawit.mysawit_auth.repository.AuthRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
    void validateRequiredFieldsBlankUsername() {
        assertThrows(InvalidCredentialException.class, () -> validator.validateRequiredFields("", "Budi", "budi@gmail.com", Role.SUPIR));
    }

    @Test
    void validateRequiredFieldsNullRole() {
        assertThrows(InvalidCredentialException.class, () -> validator.validateRequiredFields("Admin Sawit", "Agus", "admin@gmail.com", null));
    }

    @Test
    void validatePasswordSuccess() {
        assertDoesNotThrow(() -> validator.validatePassword("Admin123!"));
        assertDoesNotThrow(() -> validator.validatePassword("Sup1r@Sawit"));
        assertDoesNotThrow(() -> validator.validatePassword("Mandor#99"));
    }

    @Test
    void validatePasswordNull() {
        assertThrows(InvalidCredentialException.class, () -> validator.validatePassword(null));
    }

    @Test
    void validatePasswordBlank() {
        assertThrows(InvalidCredentialException.class, () -> validator.validatePassword(""));
    }

    @Test
    void validatePasswordWhitespaceOnly() {
        assertThrows(InvalidCredentialException.class, () -> validator.validatePassword("   "));
    }

    @Test
    void validatePasswordTooShort() {
        final WeakPasswordException ex = assertThrows(WeakPasswordException.class, () -> validator.validatePassword("Ab1!"));
        assertEquals("Password must be at least 8 characters", ex.getMessage());
    }

    @Test
    void validatePasswordExactlyMinLength() {
        assertDoesNotThrow(() -> validator.validatePassword("Admin1!a"));
    }

    @Test
    void validatePasswordTooLong() {
        final String tooLong = "A1!" + "a".repeat(71);
        final WeakPasswordException ex = assertThrows(WeakPasswordException.class, () -> validator.validatePassword(tooLong));
        assertEquals("Password must not exceed 72 characters", ex.getMessage());
    }

    @Test
    void validatePasswordExactlyMaxLength() {
        final String exactMax = "A1!" + "a".repeat(69);
        assertDoesNotThrow(() -> validator.validatePassword(exactMax));
    }

    @Test
    void validatePasswordMissingUppercase() {
        final WeakPasswordException ex = assertThrows(WeakPasswordException.class, () -> validator.validatePassword("admin123!"));
        assertEquals("Password must contain at least one uppercase letter", ex.getMessage());
    }

    @Test
    void validatePasswordMissingLowercase() {
        final WeakPasswordException ex = assertThrows(WeakPasswordException.class, () -> validator.validatePassword("ADMIN123!"));
        assertEquals("Password must contain at least one lowercase letter", ex.getMessage());
    }

    @Test
    void validatePasswordMissingDigit() {
        final WeakPasswordException ex = assertThrows(WeakPasswordException.class, () -> validator.validatePassword("AdminPass!"));
        assertEquals("Password must contain at least one digit", ex.getMessage());
    }

    @Test
    void validatePasswordMissingSpecialChar() {
        final WeakPasswordException ex = assertThrows(WeakPasswordException.class, () -> validator.validatePassword("Admin1234"));
        assertEquals("Password must contain at least one special character", ex.getMessage());
    }

    @Test
    void validatePasswordTooShortTakesPriorityOverMissingUppercase() {
        final WeakPasswordException ex = assertThrows(WeakPasswordException.class, () -> validator.validatePassword("ab1!"));
        assertEquals("Password must be at least 8 characters", ex.getMessage());
    }

    @Test
    void validatePasswordTooLongTakesPriorityOverMissingUppercase() {
        final String tooLong = "a1!" + "a".repeat(71);
        final WeakPasswordException ex = assertThrows(WeakPasswordException.class, () -> validator.validatePassword(tooLong));
        assertEquals("Password must not exceed 72 characters", ex.getMessage());
    }

    @Test
    void assertEmailUniqueSuccess() {
        when(authRepository.findByEmail("usep@gmail.com")).thenReturn(null);
        assertDoesNotThrow(() -> validator.assertEmailUnique("usep@gmail.com"));
    }

    @Test
    void assertEmailUniqueDuplicate() {
        when(authRepository.findByEmail("admin@gmail.com")).thenReturn(Optional.of(new User()));
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