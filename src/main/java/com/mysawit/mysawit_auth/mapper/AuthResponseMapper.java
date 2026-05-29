package com.mysawit.mysawit_auth.mapper;

import com.mysawit.mysawit_auth.dto.UserSummary;
import com.mysawit.mysawit_auth.dto.response.AuthResponse;
import com.mysawit.mysawit_auth.dto.response.UserDetailResponse;
import com.mysawit.mysawit_auth.model.User;
import org.springframework.stereotype.Component;

import java.util.List;

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

    public UserDetailResponse toDetailResponse(
            final User user,
            final List<User> buruhList,
            final User mandorUser) {

        final List<UserSummary> assignedBuruh = (buruhList == null)
                ? null
                : buruhList.stream()
                .map(this::toSummary)
                .toList();

        final UserSummary mandor = (mandorUser == null)
                ? null
                : toSummary(mandorUser);

        return UserDetailResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .nomorSertifMandor(user.getNomorSertifMandor())
                .assignedBuruh(assignedBuruh)
                .mandor(mandor)
                .build();
    }

    public UserSummary toSummary(final User user) {
        return UserSummary.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
