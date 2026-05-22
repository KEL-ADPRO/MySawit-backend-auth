package com.mysawit.mysawit_auth.validator;

import com.mysawit.mysawit_auth.exception.EmailAlreadyExistsException;
import com.mysawit.mysawit_auth.exception.InvalidCredentialException;
import com.mysawit.mysawit_auth.exception.MandorSertifMissingException;
import com.mysawit.mysawit_auth.exception.WeakPasswordException;
import com.mysawit.mysawit_auth.model.Role;
import com.mysawit.mysawit_auth.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegistrationValidator {
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 72;
    private static final String SPECIAL_CHARACTERS = "!@#$%^&*()-_=+[]{}|;:,.<>?";

    private final AuthRepository authRepository;

    public void validateRequiredFields(final String username, final String name, final String email, final Role role) {
        if (isBlank(username) || isBlank(name) || isBlank(email) || role == null) {
            throw new InvalidCredentialException();
        }
    }

    public void validatePassword(final String password) {
        if (isBlank(password)) {
            throw new InvalidCredentialException();
        }

        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new WeakPasswordException("Password must be at least 8 characters");
        }

        if (password.length() > MAX_PASSWORD_LENGTH) {
            throw new WeakPasswordException("Password must not exceed 72 characters");
        }

        if (!containsUppercase(password)) {
            throw new WeakPasswordException("Password must contain at least one uppercase letter");
        }

        if (!containsLowercase(password)) {
            throw new WeakPasswordException("Password must contain at least one lowercase letter");
        }

        if (!containsDigit(password)) {
            throw new WeakPasswordException("Password must contain at least one digit");
        }

        if (!containsSpecialCharacter(password)) {
            throw new WeakPasswordException("Password must contain at least one special character");
        }
    }

    public void assertEmailUnique(final String email) {
        if (authRepository.findByEmail(email).isPresent()) {
            throw new EmailAlreadyExistsException(email);
        }
    }

    public void assertMandorCertPresent(final Role role, final String nomorSertifMandor) {
        if (role == Role.MANDOR && isBlank(nomorSertifMandor)) {
            throw new MandorSertifMissingException();
        }
    }

    private boolean isBlank(final String value) {
        return value == null || value.isBlank();
    }

    private boolean containsUppercase(final String password) {
        return password.chars().anyMatch(Character::isUpperCase);
    }

    private boolean containsLowercase(final String password) {
        return password.chars().anyMatch(Character::isLowerCase);
    }

    private boolean containsDigit(final String password) {
        return password.chars().anyMatch(Character::isDigit);
    }

    private boolean containsSpecialCharacter(final String password) {
        return password.chars().anyMatch(c -> SPECIAL_CHARACTERS.indexOf(c) >= 0);
    }
}