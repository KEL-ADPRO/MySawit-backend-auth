package com.mysawit.mysawit_auth.util;

import com.mysawit.mysawit_auth.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private UUID userId;
    private String username;
    private String name;
    private String email;
    private Role role;
}