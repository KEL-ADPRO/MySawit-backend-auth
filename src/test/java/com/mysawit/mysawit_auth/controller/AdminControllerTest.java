package com.mysawit.mysawit_auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysawit.mysawit_auth.dto.UserSummary;
import com.mysawit.mysawit_auth.dto.request.AssignRequest;
import com.mysawit.mysawit_auth.service.AdminService;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    private ObjectMapper objectMapper;
    private UUID buruhId;
    private UUID mandorId;
    private String token;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
        objectMapper = new ObjectMapper();
        buruhId = UUID.randomUUID();
        mandorId = UUID.randomUUID();
        token = "valid-jwt-token";
    }

    @Test
    void assignBuruhSuccess() throws Exception {
        AssignRequest request = AssignRequest.builder()
                .mandorId(mandorId)
                .build();

        UserSummary summary = new UserSummary();
        when(adminService.assignBuruhToMandor(eq(token), eq(buruhId), eq(mandorId))).thenReturn(summary);

        mockMvc.perform(put("/api/admin/buruh/{buruhId}/assign", buruhId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Buruh assigned/reassigned successfully"));

        verify(adminService).assignBuruhToMandor(token, buruhId, mandorId);
    }

    @Test
    void assignBuruhMissingAuthorizationHeader() {
        AssignRequest request = AssignRequest.builder()
                .mandorId(mandorId)
                .build();

        assertThrows(ServletException.class, () -> mockMvc.perform(put("/api/admin/buruh/{buruhId}/assign", buruhId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))));
    }

    @Test
    void unassignBuruhSuccess() throws Exception {
        UserSummary summary = new UserSummary();
        when(adminService.unassignBuruh(eq(token), eq(buruhId))).thenReturn(summary);

        mockMvc.perform(delete("/api/admin/buruh/{buruhId}/assign", buruhId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Buruh unassigned successfully"));

        verify(adminService).unassignBuruh(token, buruhId);
    }

    @Test
    void deleteUserSuccess() throws Exception {
        UUID targetUserId = UUID.randomUUID();
        doNothing().when(adminService).deleteUser(eq(token), eq(targetUserId));

        mockMvc.perform(delete("/api/admin/users/{userId}", targetUserId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted successfully"));

        verify(adminService).deleteUser(token, targetUserId);
    }
}