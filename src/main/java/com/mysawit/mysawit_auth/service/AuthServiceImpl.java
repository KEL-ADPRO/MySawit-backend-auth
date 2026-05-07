package com.mysawit.mysawit_auth.service;

import com.mysawit.mysawit_auth.exception.EmailAlreadyExistsException;
import com.mysawit.mysawit_auth.exception.InvalidCredentialException;
import com.mysawit.mysawit_auth.exception.MandorSertifMissingException;
import com.mysawit.mysawit_auth.model.Role;
import com.mysawit.mysawit_auth.model.User;
import com.mysawit.mysawit_auth.repository.AuthRepository;
import com.mysawit.mysawit_auth.util.*;
import com.mysawit.mysawit_auth.dto.request.LoginRequest;
import com.mysawit.mysawit_auth.dto.request.RegisterRequest;
import com.mysawit.mysawit_auth.dto.response.AuthResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthRepository authRepository;
    private final PasswordHasher passwordHasher;
    private final JwtUtil jwtUtil;
    private final TokenBlacklist tokenBlacklist;

    @Override
    @Transactional
    public AuthResponse register(final RegisterRequest request) {
        if (request == null || request.getUsername() == null ||
                request.getUsername().isBlank() || request.getName() == null ||
                request.getName().isBlank() || request.getEmail() == null ||
                request.getEmail().isBlank() || request.getPassword() == null ||
                request.getPassword().isBlank() || request.getRole() == null) {
            throw new InvalidCredentialException();
        }

        guardEmailUnique(request.getEmail());
        guardMandorCertification(request);
        final User saved = authRepository.save(buildUser(request));
        return toResponse(saved, null);
    }

    @Override
    public AuthResponse login(final LoginRequest request) {
        if (request == null || request.getEmail() == null || request.getEmail().isBlank() ||
                request.getPassword() == null || request.getPassword().isBlank()) {
            throw new InvalidCredentialException();
        }

        final User user = authRepository.findByEmail(request.getEmail());
        if (user == null || !passwordHasher.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialException();
        }

        if (user.getGoogleId() != null) {
            throw new IllegalArgumentException("This account uses Google login. Please sign in with Google.");
        }

        final String token = jwtUtil.generateToken(user.getId(), user.getRole());
        return toResponse(user, token);
    }

    @Override
    public AuthResponse getLoggedInUser(final String token) {
        if (tokenBlacklist.isBlacklisted(token)) {
            throw new InvalidCredentialException();
        }

        final String userId = jwtUtil.extractUserId(token);
        final User user = authRepository.findById(UUID.fromString(userId));

        if (user == null) {
            throw new InvalidCredentialException();
        }
        return toResponse(user, token);
    }

    @Override
    public void logout(final String token) {
        if (token == null || token.isBlank()) {
            throw new InvalidCredentialException();
        }

        try {
            jwtUtil.extractUserId(token);
        } catch (Exception e) {
            throw new InvalidCredentialException();
        }

        tokenBlacklist.blacklist(token);
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