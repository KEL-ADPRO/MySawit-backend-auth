package com.mysawit.mysawit_auth.controller;

import com.mysawit.mysawit_auth.dto.request.AssignRequest;
import com.mysawit.mysawit_auth.dto.response.ApiResponse;
import com.mysawit.mysawit_auth.dto.response.AuthResponse;
import com.mysawit.mysawit_auth.mapper.AuthResponseMapper;
import com.mysawit.mysawit_auth.model.Role;
import com.mysawit.mysawit_auth.model.User;
import com.mysawit.mysawit_auth.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final AuthResponseMapper responseMapper;

    /**
     * GET /api/admin/users
     * Query params: name, email, role (semua opsional)
     * Mengembalikan daftar semua user, dengan filter opsional.
     */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<AuthResponse>>> getUsers(
            @RequestHeader(value = "Authorization", required = false) final String authHeader,
            @RequestParam(required = false) final String name,
            @RequestParam(required = false) final String email,
            @RequestParam(required = false) final String role) {

        final String token = extractBearer(authHeader);
        final Role roleEnum = parseRole(role);
        final List<User> users = adminService.getUsersWithFilters(token, name, email, roleEnum);

        final List<AuthResponse> responseList = users.stream()
                .map(u -> responseMapper.toResponse(u, null, null))
                .toList();

        return ResponseEntity.ok(ApiResponse.successResponse("Users fetched successfully", responseList));
    }

    /**
     * GET /api/admin/users/{userId}
     * Mengembalikan detail satu user berdasarkan ID.
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<AuthResponse>> getUserById(
            @RequestHeader(value = "Authorization", required = false) final String authHeader,
            @PathVariable final UUID userId) {

        final String token = extractBearer(authHeader);
        final User user = adminService.getUserById(token, userId);
        return ResponseEntity.ok(ApiResponse.successResponse("User fetched successfully", responseMapper.toResponse(user, null, null)));
    }

    /**
     * PUT /api/admin/buruh/{buruhId}/assign
     * Assign buruh ke mandor.
     */
    @PutMapping("/buruh/{buruhId}/assign")
    public ResponseEntity<ApiResponse<AuthResponse>> assignBuruh(
            @RequestHeader(value = "Authorization", required = false) final String authHeader,
            @PathVariable final UUID buruhId,
            @Valid @RequestBody final AssignRequest request) {
        final String token = extractBearer(authHeader);
        final AuthResponse response = adminService.assignBuruhToMandor(token, buruhId, request.getMandorId());
        return ResponseEntity.ok(ApiResponse.successResponse("Buruh assigned/reassigned successfully", response));
    }

    /**
     * DELETE /api/admin/buruh/{buruhId}/assign
     * Unassign buruh dari mandor.
     */
    @DeleteMapping("/buruh/{buruhId}/assign")
    public ResponseEntity<ApiResponse<AuthResponse>> unassignBuruh(
            @RequestHeader(value = "Authorization", required = false) final String authHeader,
            @PathVariable final UUID buruhId) {
        final String token = extractBearer(authHeader);
        final AuthResponse response = adminService.unassignBuruh(token, buruhId);
        return ResponseEntity.ok(ApiResponse.successResponse("Buruh unassigned successfully", response));
    }

    /**
     * DELETE /api/admin/users/{userId}
     * Hapus user dari sistem.
     */
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @RequestHeader(value = "Authorization", required = false) final String authHeader,
            @PathVariable final UUID userId) {
        final String token = extractBearer(authHeader);
        adminService.deleteUser(token, userId);
        return ResponseEntity.ok(ApiResponse.successResponse("User deleted successfully", null));
    }

    private String extractBearer(final String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid Authorization header");
        }
        return authHeader.substring(7);
    }

    private Role parseRole(final String role) {
        if (role == null || role.isBlank()) return null;
        try {
            return Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null; // role tidak valid → abaikan filter
        }
    }
}