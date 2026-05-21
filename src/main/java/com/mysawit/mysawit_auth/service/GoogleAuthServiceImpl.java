package com.mysawit.mysawit_auth.service;

import com.mysawit.mysawit_auth.dto.request.GoogleAuthRequest;
import com.mysawit.mysawit_auth.dto.GoogleUserInfo;
import com.mysawit.mysawit_auth.dto.response.AuthResponse;
import com.mysawit.mysawit_auth.exception.EmailAlreadyExistsException;
import com.mysawit.mysawit_auth.mapper.AuthResponseMapper;
import com.mysawit.mysawit_auth.model.AuthProvider;
import com.mysawit.mysawit_auth.model.User;
import com.mysawit.mysawit_auth.repository.AuthRepository;
import com.mysawit.mysawit_auth.service.strategy.AuthStrategy;
import com.mysawit.mysawit_auth.util.GoogleTokenVerifier;
import com.mysawit.mysawit_auth.util.JwtUtil;
import com.mysawit.mysawit_auth.validator.RegistrationValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoogleAuthServiceImpl implements GoogleAuthService, AuthStrategy<GoogleAuthRequest> {
    private final AuthRepository authRepository;
    private final GoogleTokenVerifier googleTokenVerifier;
    private final JwtUtil jwtUtil;
    private final RegistrationValidator registrationValidator;
    private final AuthResponseMapper responseMapper;

    @Override
    @Transactional
    public AuthResponse loginOrRegister(final GoogleAuthRequest request) {
        final GoogleUserInfo userInfo = googleTokenVerifier.verify(request.getIdToken());
        User user = authRepository.findByGoogleId(userInfo.getGoogleId());

        if (user == null) {
            user = resolveByEmailOrCreate(request, userInfo);
        }

        final String token = jwtUtil.generateToken(user.getId(), user.getRole());
        return responseMapper.toResponse(user, token);
    }

    @Override
    @Transactional
    public AuthResponse authenticate(GoogleAuthRequest request) {
        return this.loginOrRegister(request);
    }

    @Override
    public AuthProvider getProviderType() {
        return AuthProvider.GOOGLE;
    }

    private User resolveByEmailOrCreate(final GoogleAuthRequest request, final GoogleUserInfo userInfo) {
        final User existingByEmail = authRepository.findByEmail(userInfo.getEmail());
        if (existingByEmail != null) {
            throw new EmailAlreadyExistsException(userInfo.getEmail());
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

}
