package com.mysawit.mysawit_auth.controller;

import com.mysawit.mysawit_auth.dto.request.AssignRequest;
import com.mysawit.mysawit_auth.dto.response.ApiResponse;
import com.mysawit.mysawit_auth.dto.response.AuthResponse;
import com.mysawit.mysawit_auth.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @PutMapping("/buruh/{buruhId}/assign")
    public ResponseEntity<ApiResponse<AuthResponse>> assignBuruh(
            @RequestHeader(value = "Authorization", required = false) final String authHeader,
            @PathVariable final UUID buruhId,
            @Valid @RequestBody final AssignRequest request) {

        final String token = extractBearer(authHeader);
        final AuthResponse response = adminService.assignBuruhToMandor(token, buruhId, request.getMandorId());
        return ResponseEntity.ok(ApiResponse.successResponse("Buruh assigned successfully", response));
    }

    @DeleteMapping("/buruh/{buruhId}/assign")
    public ResponseEntity<ApiResponse<AuthResponse>> unassignBuruh(
            @RequestHeader(value = "Authorization", required = false) final String authHeader,
            @PathVariable final UUID buruhId) {

        final String token = extractBearer(authHeader);
        final AuthResponse response = adminService.unassignBuruh(token, buruhId);
        return ResponseEntity.ok(ApiResponse.successResponse("Buruh unassigned successfully", response));
    }

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
}