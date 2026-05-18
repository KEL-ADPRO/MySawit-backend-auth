package com.mysawit.mysawit_auth.service;

import com.mysawit.mysawit_auth.dto.request.GoogleAuthRequest;
import com.mysawit.mysawit_auth.dto.GoogleUserInfo;
import com.mysawit.mysawit_auth.dto.response.AuthResponse;
import com.mysawit.mysawit_auth.model.User;
import com.mysawit.mysawit_auth.repository.AuthRepository;
import com.mysawit.mysawit_auth.util.GoogleTokenVerifier;
import com.mysawit.mysawit_auth.util.JwtUtil;
import com.mysawit.mysawit_auth.validator.RegistrationValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoogleAuthServiceImpl implements GoogleAuthService {
    private final AuthRepository authRepository;
    private final GoogleTokenVerifier googleTokenVerifier;
    private final JwtUtil jwtUtil;
    private final RegistrationValidator registrationValidator;

    @Override
    @Transactional
    public AuthResponse loginOrRegister(final GoogleAuthRequest request) {
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
        registrationValidator.validateRequiredFields(request.getUsername(), userInfo.getName(), userInfo.getEmail(), request.getRole());
        registrationValidator.assertEmailUnique(userInfo.getEmail());
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
