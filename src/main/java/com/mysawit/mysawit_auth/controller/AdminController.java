package com.mysawit.mysawit_auth.controller;

import com.mysawit.mysawit_auth.dto.UserSummary;
import com.mysawit.mysawit_auth.dto.request.AssignRequest;
import com.mysawit.mysawit_auth.dto.response.ApiResponse;
import com.mysawit.mysawit_auth.dto.response.UserDetailResponse;
import com.mysawit.mysawit_auth.model.Role;
import com.mysawit.mysawit_auth.service.AdminService;
import com.mysawit.mysawit_auth.util.BearerTokenExtractor;
import com.mysawit.mysawit_auth.util.CookieUtil;
import com.mysawit.mysawit_auth.util.UserFilter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserSummary>>> getUsers(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) final String authHeader,
            @CookieValue(value = CookieUtil.AUTH_COOKIE_NAME, required = false) final String cookieToken,
            @RequestParam(required = false) final String name,
            @RequestParam(required = false) final String email,
            @RequestParam(required = false) final String role) {

        final String token = BearerTokenExtractor.resolve(authHeader, cookieToken);
        final UserFilter filter = new UserFilter(name, email, parseRole(role));
        final List<UserSummary> users = adminService.getUsersWithFilters(token, filter);
        return ResponseEntity.ok(ApiResponse.successResponse("Users fetched successfully", users));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<UserDetailResponse>> getUserById(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) final String authHeader,
            @CookieValue(value = CookieUtil.AUTH_COOKIE_NAME, required = false) final String cookieToken,
            @PathVariable final UUID userId) {

        final String token = BearerTokenExtractor.resolve(authHeader, cookieToken);
        final UserDetailResponse detail = adminService.getUserById(token, userId);
        return ResponseEntity.ok(ApiResponse.successResponse("User fetched successfully", detail));
    }

    @PutMapping("/buruh/{buruhId}/assign")
    public ResponseEntity<ApiResponse<UserSummary>> assignBuruh(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) final String authHeader,
            @CookieValue(value = CookieUtil.AUTH_COOKIE_NAME, required = false) final String cookieToken,
            @PathVariable final UUID buruhId,
            @Valid @RequestBody final AssignRequest request) {

        final String token = BearerTokenExtractor.resolve(authHeader, cookieToken);
        final UserSummary response = adminService.assignBuruhToMandor(token, buruhId, request.getMandorId());
        return ResponseEntity.ok(ApiResponse.successResponse("Buruh assigned/reassigned successfully", response));
    }

    @DeleteMapping("/buruh/{buruhId}/assign")
    public ResponseEntity<ApiResponse<UserSummary>> unassignBuruh(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) final String authHeader,
            @CookieValue(value = CookieUtil.AUTH_COOKIE_NAME, required = false) final String cookieToken,
            @PathVariable final UUID buruhId) {

        final String token = BearerTokenExtractor.resolve(authHeader, cookieToken);
        final UserSummary response = adminService.unassignBuruh(token, buruhId);
        return ResponseEntity.ok(ApiResponse.successResponse("Buruh unassigned successfully", response));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) final String authHeader,
            @CookieValue(value = CookieUtil.AUTH_COOKIE_NAME, required = false) final String cookieToken,
            @PathVariable final UUID userId) {

        final String token = BearerTokenExtractor.resolve(authHeader, cookieToken);
        adminService.deleteUser(token, userId);
        return ResponseEntity.ok(ApiResponse.successResponse("User deleted successfully", null));
    }

    private Role parseRole(final String role) {
        if (role == null || role.isBlank()) return null;
        return Role.fromString(role);
    }
}