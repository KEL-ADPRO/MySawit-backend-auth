package com.mysawit.mysawit_auth.service.strategy;

import com.mysawit.mysawit_auth.dto.request.AuthRequest;
import com.mysawit.mysawit_auth.dto.response.AuthResponse;
import com.mysawit.mysawit_auth.exception.InvalidCredentialException;
import com.mysawit.mysawit_auth.mapper.AuthResponseMapper;
import com.mysawit.mysawit_auth.model.AuthProvider;
import com.mysawit.mysawit_auth.model.RefreshToken;
import com.mysawit.mysawit_auth.model.User;
import com.mysawit.mysawit_auth.repository.AuthRepository;
import com.mysawit.mysawit_auth.service.LoginAttemptService;
import com.mysawit.mysawit_auth.service.RefreshTokenService;
import com.mysawit.mysawit_auth.util.JwtUtil;
import com.mysawit.mysawit_auth.util.PasswordHasher;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PasswordAuthStrategy implements AuthStrategy {
    private final AuthRepository authRepository;
    private final PasswordHasher passwordHasher;
    private final JwtUtil jwtUtil;
    private final AuthResponseMapper responseMapper;
    private final LoginAttemptService loginAttemptService;
    private final RefreshTokenService refreshTokenService;

    @Override
    @Transactional
    public AuthResponse authenticate(final AuthRequest request) {
        if (request == null
                || request.getEmail() == null || request.getEmail().isBlank()
                || request.getPassword() == null || request.getPassword().isBlank()) {
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
    public AuthProvider getProviderType() {
        return AuthProvider.PASSWORD;
    }
}