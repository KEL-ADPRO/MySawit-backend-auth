package com.mysawit.mysawit_auth.dto;

import com.mysawit.mysawit_auth.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSummary {
    private UUID userId;
    private String username;
    private String name;
    private String email;
    private Role role;
}
