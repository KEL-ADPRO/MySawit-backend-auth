package com.mysawit.mysawit_auth.service;

import com.mysawit.mysawit_auth.exception.InvalidCredentialException;
import com.mysawit.mysawit_auth.model.Role;
import com.mysawit.mysawit_auth.util.JwtUtil;
import com.mysawit.mysawit_auth.util.TokenBlacklist;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenAuthorizationServiceImpl implements TokenAuthorizationService {

    private final JwtUtil jwtUtil;
    private final TokenBlacklist tokenBlacklist;

    @Override
    public UUID requireRole(final String token, final Role requiredRole) {
        if (token == null || token.isBlank()) {
            throw new InvalidCredentialException();
        }

        if (tokenBlacklist.isBlacklisted(token)) {
            throw new InvalidCredentialException();
        }

        final UUID callerId;
        final String roleString;
        try {
            callerId = UUID.fromString(jwtUtil.extractUserId(token));
            roleString = jwtUtil.extractRole(token);
        } catch (Exception e) {
            final InvalidCredentialException ex = new InvalidCredentialException();
            ex.initCause(e);
            throw ex;
        }

        if (!requiredRole.name().equals(roleString)) {
            throw new IllegalArgumentException(
                    "Access denied: " + requiredRole.name() + " role required");
        }

        return callerId;
    }
}