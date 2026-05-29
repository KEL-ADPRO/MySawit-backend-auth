package com.mysawit.mysawit_auth.service;

import com.mysawit.mysawit_auth.dto.UserSummary;
import com.mysawit.mysawit_auth.dto.response.AuthResponse;
import com.mysawit.mysawit_auth.dto.response.UserDetailResponse;
import com.mysawit.mysawit_auth.model.Role;

import java.util.List;
import java.util.UUID;

public interface AdminService {
    AuthResponse assignBuruhToMandor(String adminToken, UUID buruhId, UUID mandorId);
    AuthResponse unassignBuruh(String adminToken, UUID buruhId);
    void deleteUser(String adminToken, UUID targetUserId);
    List<UserSummary> getUsersWithFilters(String adminToken, String name, String email, Role role);
    UserDetailResponse getUserById(String adminToken, UUID userId);
}