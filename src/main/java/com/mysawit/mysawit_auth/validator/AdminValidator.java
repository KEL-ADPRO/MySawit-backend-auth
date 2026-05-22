package com.mysawit.mysawit_auth.validator;

import com.mysawit.mysawit_auth.model.Role;
import com.mysawit.mysawit_auth.model.User;
import com.mysawit.mysawit_auth.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AdminValidator {
    private final AuthRepository authRepository;

    public User requireBuruh(final UUID buruhId) {
        final User user = authRepository.findById(buruhId);
        if (user == null) {
            throw new IllegalArgumentException("Buruh not found: " + buruhId);
        }
        if (user.getRole() != Role.BURUH) {
            throw new IllegalArgumentException("User " + buruhId + " is not a Buruh");
        }
        return user;
    }

    public User requireMandor(final UUID mandorId) {
        final User user = authRepository.findById(mandorId);
        if (user == null) {
            throw new IllegalArgumentException("Mandor not found: " + mandorId);
        }
        if (user.getRole() != Role.MANDOR) {
            throw new IllegalArgumentException("User " + mandorId + " is not a Mandor");
        }
        return user;
    }
}