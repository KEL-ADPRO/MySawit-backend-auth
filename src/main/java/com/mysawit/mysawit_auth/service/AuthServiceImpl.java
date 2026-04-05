package com.mysawit.mysawit_auth.service;

import com.mysawit.mysawit_auth.exception.EmailAlreadyExistsException;
import com.mysawit.mysawit_auth.exception.MandorSertifMissingException;
import com.mysawit.mysawit_auth.model.Role;
import com.mysawit.mysawit_auth.model.User;
import com.mysawit.mysawit_auth.repository.AuthRepository;
import com.mysawit.mysawit_auth.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    @Autowired
    private final AuthRepository authRepository;
    private final PasswordHasher passwordHasher;
    private final JwtUtil jwtUtil;

    @Override
    public AuthResponse register(final RegisterRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request must not be null!");
        } else if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username must not be blank!");
        } else if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Name must not be blank!");
        } else if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email must not be blank!");
        } else if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password must not be blank!");
        } else if (request.getRole() == null) {
            throw new IllegalArgumentException("Role must not be null!");
        }

        guardEmailUnique(request.getEmail());
        guardMandorCertification(request);
        final User saved = authRepository.save(buildUser(request));
        return toResponse(saved, null);
    }

    @Override
    public AuthResponse login(final LoginRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request must not be null!");
        } else if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email must not be blank!");
        } else if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password must not be blank!");
        }

        final User user = authRepository.findByEmail(request.getEmail());
        if (user == null || !passwordHasher.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password!");
        }

        final String token = jwtUtil.generateToken(user.getEmail());
        return toResponse(user, token);
    }

    private void guardEmailUnique(final String email) {
        if (authRepository.findByEmail(email) != null) {
            throw new EmailAlreadyExistsException(email);
        }
    }

    private void guardMandorCertification(final RegisterRequest request) {
        final boolean isMandor = request.getRole() == Role.MANDOR;
        final boolean missingCert = request.getNomorSertifMandor() == null || request.getNomorSertifMandor().isBlank();

        if (isMandor && missingCert) {
            throw new MandorSertifMissingException();
        }
    }

    private User buildUser(final RegisterRequest request) {
        return User.builder()
                .username(request.getUsername())
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordHasher.hash(request.getPassword()))
                .role(request.getRole())
                .nomorSertifMandor(request.getNomorSertifMandor())
                .build();
    }

    private AuthResponse toResponse(final User user, final String token) {
        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .nomorSertifMandor(user.getNomorSertifMandor())
                .build();
    }
}