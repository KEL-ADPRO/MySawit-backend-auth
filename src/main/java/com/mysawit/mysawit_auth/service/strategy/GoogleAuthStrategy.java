package com.mysawit.mysawit_auth.service.strategy;

import com.mysawit.mysawit_auth.dto.GoogleUserInfo;
import com.mysawit.mysawit_auth.dto.request.AuthRequest;
import com.mysawit.mysawit_auth.dto.response.AuthResponse;
import com.mysawit.mysawit_auth.exception.EmailAlreadyExistsException;
import com.mysawit.mysawit_auth.mapper.AuthResponseMapper;
import com.mysawit.mysawit_auth.model.AuthProvider;
import com.mysawit.mysawit_auth.model.RefreshToken;
import com.mysawit.mysawit_auth.model.User;
import com.mysawit.mysawit_auth.repository.AuthRepository;
import com.mysawit.mysawit_auth.service.RefreshTokenService;
import com.mysawit.mysawit_auth.util.GoogleTokenVerifier;
import com.mysawit.mysawit_auth.util.JwtUtil;
import com.mysawit.mysawit_auth.validator.RegistrationValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GoogleAuthStrategy implements AuthStrategy {
    private final AuthRepository authRepository;
    private final GoogleTokenVerifier googleTokenVerifier;
    private final JwtUtil jwtUtil;
    private final RegistrationValidator registrationValidator;
    private final AuthResponseMapper responseMapper;
    private final RefreshTokenService refreshTokenService;

    @Override
    @Transactional
    public AuthResponse authenticate(final AuthRequest request) {
        final GoogleUserInfo userInfo = googleTokenVerifier.verify(request.getIdToken());
        User user = authRepository.findByGoogleId(userInfo.getGoogleId());

        if (user == null) {
            user = resolveByEmailOrCreate(request, userInfo);
        }

        final String accessToken = jwtUtil.generateToken(user.getId(), user.getRole());
        final RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        return responseMapper.toResponse(user, accessToken, refreshToken.getToken());
    }

    @Override
    public AuthProvider getProviderType() {
        return AuthProvider.GOOGLE;
    }

    private User resolveByEmailOrCreate(final AuthRequest request, final GoogleUserInfo userInfo) {
        final Optional<User> existingByEmail = authRepository.findByEmail(userInfo.getEmail());
        if (existingByEmail.isPresent()) {
            throw new EmailAlreadyExistsException(userInfo.getEmail());
        }
        return createNewUser(request, userInfo);
    }

    private User createNewUser(final AuthRequest request, final GoogleUserInfo userInfo) {
        registrationValidator.validateRequiredFields(request.getUsername(), userInfo.getName(), userInfo.getEmail(), request.getRole());
        registrationValidator.assertEmailUnique(userInfo.getEmail());
        registrationValidator.assertUsernameUnique(request.getUsername());
        registrationValidator.assertMandorCertPresent(request.getRole(), request.getNomorSertifMandor());

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
}