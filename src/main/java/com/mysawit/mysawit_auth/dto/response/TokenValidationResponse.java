package com.mysawit.mysawit_auth.dto.response;

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
public class TokenValidationResponse {
    private boolean valid;
    private UUID userId;
    private Role role;
}
