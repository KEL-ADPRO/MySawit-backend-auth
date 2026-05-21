package com.mysawit.mysawit_auth.service;

import com.mysawit.mysawit_auth.exception.InvalidCredentialException;
import com.mysawit.mysawit_auth.mapper.AuthResponseMapper;
import com.mysawit.mysawit_auth.model.AuthProvider;
import com.mysawit.mysawit_auth.model.User;
import com.mysawit.mysawit_auth.repository.AuthRepository;
import com.mysawit.mysawit_auth.service.strategy.AuthStrategy;
import com.mysawit.mysawit_auth.util.*;
import com.mysawit.mysawit_auth.dto.request.LoginRequest;
import com.mysawit.mysawit_auth.dto.request.RegisterRequest;
import com.mysawit.mysawit_auth.dto.response.AuthResponse;
import com.mysawit.mysawit_auth.validator.RegistrationValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService, AuthStrategy<LoginRequest> {
    private final AuthRepository authRepository;
    private final PasswordHasher passwordHasher;
    private final JwtUtil jwtUtil;
    private final TokenBlacklist tokenBlacklist;
    private final RegistrationValidator registrationValidator;
    private final AuthResponseMapper responseMapper;
    private final LoginAttemptService loginAttemptService;

    @Override
    @Transactional
    public AuthResponse register(final RegisterRequest request) {
        if (request == null) {
            throw new InvalidCredentialException();
        }

        registrationValidator.validateRequiredFields(request.getUsername(), request.getName(), request.getEmail(), request.getRole());
        registrationValidator.validatePassword(request.getPassword());
        registrationValidator.assertEmailUnique(request.getEmail());
        registrationValidator.assertMandorCertPresent(request.getRole(), request.getNomorSertifMandor());

        final User saved = authRepository.save(buildUser(request));
        return responseMapper.toResponse(saved, null);
    }

    @Override
    public AuthResponse login(final LoginRequest request) {
        if (request == null || request.getEmail() == null || request.getEmail().isBlank() ||
                request.getPassword() == null || request.getPassword().isBlank()) {
            throw new InvalidCredentialException();
        }

        loginAttemptService.assertNotLocked(request.getEmail());

        final User user = authRepository.findByEmail(request.getEmail());
        if (user == null) {
            loginAttemptService.recordFailure(request.getEmail());
            throw new InvalidCredentialException();
        }

        if (user.getGoogleId() != null) {
            loginAttemptService.recordFailure(request.getEmail());
            throw new IllegalArgumentException("This account uses Google login. Please sign in with Google.");
        }

        if (!passwordHasher.matches(request.getPassword(), user.getPassword())) {
            loginAttemptService.recordFailure(request.getEmail());
            throw new InvalidCredentialException();
        }

        loginAttemptService.recordSuccess(request.getEmail());
        final String token = jwtUtil.generateToken(user.getId(), user.getRole());
        return responseMapper.toResponse(user, token);
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
        return responseMapper.toResponse(user, token);
    }

    @Override
    public void logout(final String token) {
        if (token == null || token.isBlank()) {
            throw new InvalidCredentialException();
        }

        try {
            jwtUtil.extractUserId(token);
        } catch (Exception exception) {
            final InvalidCredentialException thrownException = new InvalidCredentialException();
            thrownException.initCause(exception);
            throw thrownException;
        }

        tokenBlacklist.blacklist(token);
    }

    @Override
    public AuthResponse authenticate(LoginRequest request) {
        return this.login(request);
    }

    @Override
    public AuthProvider getProviderType() {
        return AuthProvider.PASSWORD;
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
}