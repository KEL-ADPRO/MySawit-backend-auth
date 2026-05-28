package com.mysawit.mysawit_auth.service;

import com.mysawit.mysawit_auth.dto.response.AuthResponse;
import com.mysawit.mysawit_auth.exception.InvalidCredentialException;
import com.mysawit.mysawit_auth.exception.SelfDeletionException;
import com.mysawit.mysawit_auth.mapper.AuthResponseMapper;
import com.mysawit.mysawit_auth.model.Role;
import com.mysawit.mysawit_auth.model.User;
import com.mysawit.mysawit_auth.repository.AuthRepository;
import com.mysawit.mysawit_auth.repository.RefreshTokenRepository;
import com.mysawit.mysawit_auth.util.JwtUtil;
import com.mysawit.mysawit_auth.util.TokenBlacklist;
import com.mysawit.mysawit_auth.validator.AdminValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final AuthRepository authRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;
    private final TokenBlacklist tokenBlacklist;
    private final AuthResponseMapper responseMapper;
    private final AdminValidator adminValidator;

    @Override
    @Transactional
    public AuthResponse assignBuruhToMandor(final String adminToken, final UUID buruhId, final UUID mandorId) {
        resolveAdminId(adminToken);

        final User buruh = adminValidator.requireBuruh(buruhId);
        final User mandor = adminValidator.requireMandor(mandorId);

        buruh.setMandorId(mandor.getId());
        final User savedUser = authRepository.save(buruh);

        return responseMapper.toResponse(savedUser, null, null);
    }

    @Override
    @Transactional
    public AuthResponse unassignBuruh(final String adminToken, final UUID buruhId) {
        resolveAdminId(adminToken);

        final User buruh = adminValidator.requireBuruh(buruhId);
        buruh.setMandorId(null);
        final User savedUser = authRepository.save(buruh);

        return responseMapper.toResponse(savedUser, null, null);
    }

    @Override
    @Transactional
    public void deleteUser(final String adminToken, final UUID targetUserId) {
        final UUID adminId = resolveAdminId(adminToken);

        if (adminId.equals(targetUserId)) {
            throw new SelfDeletionException();
        }

        final User target = authRepository.findById(targetUserId);
        if (target == null) {
            throw new IllegalArgumentException("User not found: " + targetUserId);
        }

        if (target.getRole() == Role.MANDOR) {
            authRepository.findByMandorId(targetUserId)
                    .forEach(buruh -> {
                        buruh.setMandorId(null);
                        authRepository.save(buruh);
                    });
        }

        refreshTokenRepository.deleteByUserId(targetUserId);
        authRepository.delete(targetUserId);
    }

    @Override
    public List<User> getUsersWithFilters(final String adminToken, final String name, final String email, final Role role) {
        resolveAdminId(adminToken);

        final boolean hasName  = name  != null && !name.isBlank();
        final boolean hasEmail = email != null && !email.isBlank();
        final boolean hasRole  = role  != null;

        if (hasName && hasEmail && hasRole) {
            return authRepository.findByNameAndEmailAndRole(name, email, role);
        }

        if (hasName && hasEmail) {
            return authRepository.findByNameAndEmail(name, email);
        }
        if (hasName && hasRole) {
            return authRepository.findByNameAndRole(name, role);
        }
        if (hasEmail && hasRole) {
            return authRepository.findByEmailAndRole(email, role);
        }

        if (hasName) {
            return authRepository.findByName(name);
        }
        if (hasEmail) {
            return authRepository.findByEmail(email)
                    .map(List::of)
                    .orElse(List.of());
        }
        if (hasRole) {
            return authRepository.findByRole(role);
        }

        return authRepository.findAll();
    }

    @Override
    public User getUserById(final String adminToken, final UUID userId) {
        resolveAdminId(adminToken);

        final User user = authRepository.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found: " + userId);
        }
        return user;
    }

    private UUID resolveAdminId(final String adminToken) {
        if (adminToken == null || adminToken.isBlank()) {
            throw new InvalidCredentialException();
        }

        if (tokenBlacklist.isBlacklisted(adminToken)) {
            throw new InvalidCredentialException();
        }

        final UUID callerId;
        final String roleString;
        try {
            callerId = UUID.fromString(jwtUtil.extractUserId(adminToken));
            roleString = jwtUtil.extractRole(adminToken);
        } catch (Exception e) {
            final InvalidCredentialException exception = new InvalidCredentialException();
            exception.initCause(e);
            throw exception;
        }

        if (!Role.ADMIN.name().equals(roleString)) {
            throw new IllegalArgumentException("Access denied: ADMIN role required");
        }

        return callerId;
    }
}