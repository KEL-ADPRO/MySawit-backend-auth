package com.mysawit.mysawit_auth.mapper;

import com.mysawit.mysawit_auth.dto.response.AuthResponse;
import com.mysawit.mysawit_auth.model.User;
import org.springframework.stereotype.Component;

@Component
public class AuthResponseMapper {

    public AuthResponse toResponse(final User user, final String accessToken, final String refreshToken) {
        return AuthResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .nomorSertifMandor(user.getNomorSertifMandor())
                .build();
    }
}
