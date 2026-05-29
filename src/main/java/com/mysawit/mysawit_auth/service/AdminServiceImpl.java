package com.mysawit.mysawit_auth.service;

import com.mysawit.mysawit_auth.dto.UserSummary;
import com.mysawit.mysawit_auth.dto.response.AuthResponse;
import com.mysawit.mysawit_auth.dto.response.UserDetailResponse;
import com.mysawit.mysawit_auth.exception.SelfDeletionException;
import com.mysawit.mysawit_auth.exception.UserNotFoundException;
import com.mysawit.mysawit_auth.mapper.AuthResponseMapper;
import com.mysawit.mysawit_auth.model.Role;
import com.mysawit.mysawit_auth.model.User;
import com.mysawit.mysawit_auth.repository.AuthRepository;
import com.mysawit.mysawit_auth.repository.RefreshTokenRepository;
import com.mysawit.mysawit_auth.validator.AdminValidator;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final AuthRepository authRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenAuthorizationService tokenAuthService;
    private final AuthResponseMapper responseMapper;
    private final AdminValidator adminValidator;

    @Override
    @Transactional
    public UserSummary assignBuruhToMandor(final String adminToken, final UUID buruhId, final UUID mandorId) {
        tokenAuthService.requireRole(adminToken, Role.ADMIN);

        final User buruh = adminValidator.requireBuruh(buruhId);
        final User mandor = adminValidator.requireMandor(mandorId);

        buruh.setMandorId(mandor.getId());
        final User savedUser = authRepository.save(buruh);

        return responseMapper.toSummary(savedUser);
    }

    @Override
    @Transactional
    public UserSummary unassignBuruh(final String adminToken, final UUID buruhId) {
        tokenAuthService.requireRole(adminToken, Role.ADMIN);

        final User buruh = adminValidator.requireBuruh(buruhId);
        buruh.setMandorId(null);
        final User savedUser = authRepository.save(buruh);

        return responseMapper.toSummary(savedUser);
    }

    @Override
    @Transactional
    public void deleteUser(final String adminToken, final UUID targetUserId) {
        final UUID adminId = tokenAuthService.requireRole(adminToken, Role.ADMIN);

        if (adminId.equals(targetUserId)) {
            throw new SelfDeletionException();
        }

        final User target = authRepository.findById(targetUserId);
        if (target == null) {
            throw new UserNotFoundException(targetUserId);
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
    public List<UserSummary> getUsersWithFilters(final String adminToken, final String name, final String email, final Role role) {
        tokenAuthService.requireRole(adminToken, Role.ADMIN);

        final boolean hasName = name != null && !name.isBlank();
        final boolean hasEmail = email != null && !email.isBlank();
        final boolean hasRole = role != null;

        final List<User> users;

        if (hasName && hasEmail && hasRole) {
            users = authRepository.findByNameAndEmailAndRole(name, email, role);
        } else if (hasName && hasEmail) {
            users = authRepository.findByNameAndEmail(name, email);
        } else if (hasName && hasRole) {
            users = authRepository.findByNameAndRole(name, role);
        } else if (hasEmail && hasRole) {
            users = authRepository.findByEmailAndRole(email, role);
        } else if (hasName) {
            users = authRepository.findByName(name);
        } else if (hasEmail) {
            users = authRepository.findByEmail(email).map(List::of).orElse(List.of());
        } else if (hasRole) {
            users = authRepository.findByRole(role);
        } else {
            users = authRepository.findAll();
        }

        return users.stream().map(responseMapper::toSummary).toList();
    }

    @Override
    public UserDetailResponse getUserById(final String adminToken, final UUID userId) {
        tokenAuthService.requireRole(adminToken, Role.ADMIN);

        final User user = authRepository.findById(userId);
        if (user == null) {
            throw new UserNotFoundException(userId);
        }

        return switch (user.getRole()) {
            case MANDOR -> {
                final List<User> buruhList = authRepository.findByMandorId(userId);
                yield responseMapper.toDetailResponse(user, buruhList, null);
            }
            case BURUH -> {
                final User mandor = (user.getMandorId() != null)
                        ? authRepository.findById(user.getMandorId())
                        : null;
                yield responseMapper.toDetailResponse(user, null, mandor);
            }
            default -> responseMapper.toDetailResponse(user, null, null);
        };
    }
}