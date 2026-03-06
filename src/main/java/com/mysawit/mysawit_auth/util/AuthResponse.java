package com.mysawit.mysawit_auth.util;

import com.mysawit.mysawit_auth.model.Role;
import lombok.*;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private UUID userId;
    private String username;
    private String name;
    private String email;
    private Role role;
    private String nomorSertifMandor;
}