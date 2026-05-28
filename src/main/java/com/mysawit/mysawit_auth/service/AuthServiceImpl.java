package com.mysawit.mysawit_auth.service;

import com.mysawit.mysawit_auth.dto.request.AuthRequest;
import com.mysawit.mysawit_auth.dto.request.RegisterRequest;
import com.mysawit.mysawit_auth.dto.response.AuthResponse;
import com.mysawit.mysawit_auth.exception.InvalidCredentialException;
import com.mysawit.mysawit_auth.mapper.AuthResponseMapper;
import com.mysawit.mysawit_auth.model.AuthProvider;
import com.mysawit.mysawit_auth.model.RefreshToken;
import com.mysawit.mysawit_auth.model.User;
import com.mysawit.mysawit_auth.repository.AuthRepository;
import com.mysawit.mysawit_auth.service.strategy.AuthStrategy;
import com.mysawit.mysawit_auth.service.strategy.AuthStrategyFactory;
import com.mysawit.mysawit_auth.util.JwtUtil;
import com.mysawit.mysawit_auth.util.PasswordHasher;
import com.mysawit.mysawit_auth.util.TokenBlacklist;
import com.mysawit.mysawit_auth.validator.RegistrationValidator;
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
    private final RegistrationValidator registrationValidator;
    private final AuthResponseMapper responseMapper;
    private final RefreshTokenService refreshTokenService;
    private final AuthStrategyFactory strategyFactory;

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
    public AuthResponse login(final AuthRequest request) {
        final AuthStrategy strategy = strategyFactory.resolve(AuthProvider.PASSWORD);
        return strategy.authenticate(request);
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
                refreshTokenService.revokeAll(user);
            }
        } catch (Exception exception) {
            throw (InvalidCredentialException) new InvalidCredentialException().initCause(exception);
        }

        tokenBlacklist.blacklist(token);
    }

    @Override
    @Transactional
    public AuthResponse refresh(final String rawRefreshToken) {
        final RefreshToken refreshToken = refreshTokenService.validateAndGet(rawRefreshToken);
        final UUID userId = refreshToken.getUserId();
        final User user = authRepository.findById(userId);

        refreshTokenService.revokeAll(user);
        final RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);
        final String newAccessToken = jwtUtil.generateToken(user.getId(), user.getRole());

        return responseMapper.toResponse(user, newAccessToken, newRefreshToken.getToken());
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