package com.mysawit.mysawit_auth.validator;

import com.mysawit.mysawit_auth.exception.EmailAlreadyExistsException;
import com.mysawit.mysawit_auth.exception.InvalidCredentialException;
import com.mysawit.mysawit_auth.exception.MandorSertifMissingException;
import com.mysawit.mysawit_auth.model.Role;
import com.mysawit.mysawit_auth.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegistrationValidator {
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
    }

    public void assertEmailUnique(final String email) {
        if (authRepository.findByEmail(email) != null) {
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
}