package com.mysawit.mysawit_auth.service;

import com.mysawit.mysawit_auth.dto.request.GoogleAuthRequest;
import com.mysawit.mysawit_auth.dto.request.GoogleUserInfo;
import com.mysawit.mysawit_auth.dto.response.AuthResponse;
import com.mysawit.mysawit_auth.exception.MandorSertifMissingException;
import com.mysawit.mysawit_auth.model.Role;
import com.mysawit.mysawit_auth.model.User;
import com.mysawit.mysawit_auth.repository.AuthRepository;
import com.mysawit.mysawit_auth.util.GoogleTokenVerifier;
import com.mysawit.mysawit_auth.util.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoogleAuthServiceImpl implements GoogleAuthService {
    private final AuthRepository authRepository;
    private final GoogleTokenVerifier googleTokenVerifier;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public AuthResponse loginOrRegister(final GoogleAuthRequest request) {
        guardNotNullToken(request);

        final GoogleUserInfo userInfo = googleTokenVerifier.verify(request.getIdToken());

        User user = authRepository.findByGoogleId(userInfo.getGoogleId());

        if (user == null) {
            user = resolveByEmailOrCreate(request, userInfo);
        }

        final String token = jwtUtil.generateToken(user.getId(), user.getRole());
        return toResponse(user, token);
    }

    private User resolveByEmailOrCreate(final GoogleAuthRequest request, final GoogleUserInfo userInfo) {
        final User existingByEmail = authRepository.findByEmail(userInfo.getEmail());
        if (existingByEmail != null) {
            throw new IllegalArgumentException("Email is already registered! Log in with password instead");
        }
        return createNewUser(request, userInfo);
    }

    private User createNewUser(final GoogleAuthRequest request, final GoogleUserInfo userInfo) {
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username is required for new Google accounts");
        }

        if (request.getRole() == null) {
            throw new IllegalArgumentException("Role is required for new Google accounts");
        }

        guardMandorCertification(request);

        final User newUser = User.builder()
                .googleId(userInfo.getGoogleId())
                .email(userInfo.getEmail())
                .password(null)
                .name(userInfo.getName())
                .username(request.getUsername())
                .role(request.getRole())
                .nomorSertifMandor(request.getNomorSertifMandor())
                .build();

        return authRepository.save(newUser);
    }

    private void guardNotNullToken(final GoogleAuthRequest request) {
        if (request == null || request.getIdToken() == null || request.getIdToken().isBlank()) {
            throw new IllegalArgumentException("Google ID token is required");
        }
    }

    private void guardMandorCertification(final GoogleAuthRequest request) {
        final boolean isMandor = request.getRole() == Role.MANDOR;
        final boolean missingCert = request.getNomorSertifMandor() == null || request.getNomorSertifMandor().isBlank();

        if (isMandor && missingCert) {
            throw new MandorSertifMissingException();
        }
    }

    private AuthResponse toResponse(final User user, final String token) {
        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .nomorSertifMandor(user.getNomorSertifMandor())
                .build();
    }
}
