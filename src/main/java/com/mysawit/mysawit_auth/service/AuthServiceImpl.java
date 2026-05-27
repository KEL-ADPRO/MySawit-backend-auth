package com.mysawit.mysawit_auth.service;

import com.mysawit.mysawit_auth.dto.request.LoginRequest;
import com.mysawit.mysawit_auth.dto.request.RegisterRequest;
import com.mysawit.mysawit_auth.dto.response.AuthResponse;
import com.mysawit.mysawit_auth.exception.InvalidCredentialException;
import com.mysawit.mysawit_auth.mapper.AuthResponseMapper;
import com.mysawit.mysawit_auth.model.AuthProvider;
import com.mysawit.mysawit_auth.model.RefreshToken;
import com.mysawit.mysawit_auth.model.User;
import com.mysawit.mysawit_auth.repository.AuthRepository;
import com.mysawit.mysawit_auth.repository.RefreshTokenRepository;
import com.mysawit.mysawit_auth.service.strategy.AuthStrategy;
import com.mysawit.mysawit_auth.util.JwtUtil;
import com.mysawit.mysawit_auth.util.PasswordHasher;
import com.mysawit.mysawit_auth.util.TokenBlacklist;
import com.mysawit.mysawit_auth.validator.RegistrationValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService, AuthStrategy<LoginRequest> {

    private final AuthRepository authRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordHasher passwordHasher;
    private final JwtUtil jwtUtil;
    private final TokenBlacklist tokenBlacklist;
    private final RegistrationValidator registrationValidator;
    private final AuthResponseMapper responseMapper;
    private final LoginAttemptService loginAttemptService;
    private final RefreshTokenService refreshTokenService;

    @Override
    @Transactional
    public AuthResponse register(final RegisterRequest request) {
        if (request == null) {
            throw new InvalidCredentialException();
        }

        registrationValidator.validateRequiredFields(request.getUsername(), request.getName(), request.getEmail(),
                request.getRole());
        registrationValidator.validatePassword(request.getPassword());
        registrationValidator.assertEmailUnique(request.getEmail());
        registrationValidator.assertMandorCertPresent(request.getRole(), request.getNomorSertifMandor());

        final User saved = authRepository.save(buildUser(request));
        return responseMapper.toResponse(saved, null, null);
    }

    @Override
    @Transactional
    public AuthResponse login(final LoginRequest request) {
        if (request == null || isBlank(request.getEmail()) || isBlank(request.getPassword())) {
            throw new InvalidCredentialException();
        }

        loginAttemptService.assertNotLocked(request.getEmail());

        final Optional<User> user = authRepository.findByEmail(request.getEmail());
        if (user.isEmpty()) {
            loginAttemptService.recordFailure(request.getEmail());
            throw new InvalidCredentialException();
        }

        if (user.get().getGoogleId() != null) {
            throw new IllegalArgumentException("This account uses Google login. Please sign in with Google.");
        }

        if (!passwordHasher.matches(request.getPassword(), user.get().getPassword())) {
            loginAttemptService.recordFailure(request.getEmail());
            throw new InvalidCredentialException();
        }

        loginAttemptService.recordSuccess(request.getEmail());

        final String accessToken = jwtUtil.generateToken(user.get().getId(), user.get().getRole());
        final RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.get());
        return responseMapper.toResponse(user.get(), accessToken, refreshToken.getToken());
    }

    @Override
    public AuthResponse getLoggedInUser(final String token) {
        if (token == null || token.isBlank()) {
            throw new InvalidCredentialException();
        }

        try {
            if (tokenBlacklist.isBlacklisted(token)) {
                throw new InvalidCredentialException();
            }

            final String userId = jwtUtil.extractUserId(token);
            final User user = authRepository.findById(UUID.fromString(userId));

            if (user == null) {
                throw new InvalidCredentialException();
            }

            return responseMapper.toResponse(user, token, null);

        } catch (InvalidCredentialException e) {
            throw e;
        } catch (Exception exception) {
            final InvalidCredentialException thrownException = new InvalidCredentialException();
            thrownException.initCause(exception);
            throw thrownException;
        }
    }

    @Override
    @Transactional
    public void logout(final String token) {
        if (token == null || token.isBlank()) {
            throw new InvalidCredentialException();
        }

        try {
            final String userId = jwtUtil.extractUserId(token);
            final User user = authRepository.findById(UUID.fromString(userId));
            if (user != null) {
                refreshTokenRepository.deleteByUserId(user.getId());
            }
        } catch (Exception exception) {
            final InvalidCredentialException thrownException = new InvalidCredentialException();
            thrownException.initCause(exception);
            throw thrownException;
        }

        tokenBlacklist.blacklist(token);
    }

    @Override
    @Transactional
    public AuthResponse refresh(final String rawRefreshToken) {
        final RefreshToken refreshToken = refreshTokenService.validateAndGet(rawRefreshToken);
        final UUID userId = refreshToken.getUserId();
        final User user = authRepository.findById(userId);

        final RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);
        final String newAccessToken = jwtUtil.generateToken(user.getId(), user.getRole());

        return responseMapper.toResponse(user, newAccessToken, newRefreshToken.getToken());
    }

    @Override
    @Transactional
    public AuthResponse authenticate(final LoginRequest request) {
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

    private boolean isBlank(final String value) {
        return value == null || value.isBlank();
    }
}