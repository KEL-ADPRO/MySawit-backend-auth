package com.mysawit.mysawit_auth.dto.response;

import com.mysawit.mysawit_auth.dto.UserSummary;
import com.mysawit.mysawit_auth.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailResponse {
    private UUID userId;
    private String username;
    private String name;
    private String email;
    private Role role;
    private String nomorSertifMandor;
    private List<UserSummary> assignedBuruh;
    private UserSummary mandor;
}