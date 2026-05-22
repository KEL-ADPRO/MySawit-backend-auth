package com.mysawit.mysawit_auth.service;

import com.mysawit.mysawit_auth.dto.response.AuthResponse;
import com.mysawit.mysawit_auth.exception.InvalidCredentialException;
import com.mysawit.mysawit_auth.exception.SelfDeletionException;
import com.mysawit.mysawit_auth.mapper.AuthResponseMapper;
import com.mysawit.mysawit_auth.model.Role;
import com.mysawit.mysawit_auth.model.User;
import com.mysawit.mysawit_auth.repository.AuthRepository;
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

        return responseMapper.toResponse(savedUser, null);
    }

    @Override
    @Transactional
    public AuthResponse unassignBuruh(final String adminToken, final UUID buruhId) {
        resolveAdminId(adminToken);

        final User buruh = adminValidator.requireBuruh(buruhId);
        buruh.setMandorId(null);
        final User savedUser = authRepository.save(buruh);

        return responseMapper.toResponse(savedUser, null);
    }

    @Override
    public List<User> getAllUsers() {
        return authRepository.findAll();
    }

    @Override
    public List<User> getUsersByName(final String name) {
        return authRepository.findByName(name);
    }

    @Override
    public List<User> getUserByRole(final Role role) {
        return authRepository.findByRole(role);
    }

    @Override
    public User getUsersByEmail(final String email) {
        return authRepository.findByEmail(email);
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

        authRepository.delete(targetUserId);
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
            final InvalidCredentialException ex = new InvalidCredentialException();
            ex.initCause(e);
            throw ex;
        }

        if (!Role.ADMIN.name().equals(roleString)) {
            throw new IllegalArgumentException("Access denied: ADMIN role required");
        }

        return callerId;
    }
}
