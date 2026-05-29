package com.mysawit.mysawit_auth.service;

import com.mysawit.mysawit_auth.dto.UserSummary;
import com.mysawit.mysawit_auth.dto.response.UserDetailResponse;
import com.mysawit.mysawit_auth.util.UserFilter;

import java.util.List;
import java.util.UUID;

public interface AdminService {
    UserSummary assignBuruhToMandor(String adminToken, UUID buruhId, UUID mandorId);
    UserSummary unassignBuruh(String adminToken, UUID buruhId);
    void deleteUser(String adminToken, UUID targetUserId);
    List<UserSummary> getUsersWithFilters(String adminToken, UserFilter filter);
    UserDetailResponse getUserById(String adminToken, UUID userId);
}