package com.mysawit.mysawit_auth.dto.request;

import com.mysawit.mysawit_auth.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleAuthRequest {
    @NotBlank(message = "Google ID token is required")
    private String idToken;
    private String username;
    @NotNull(message = "Role is required")
    private Role role;
    private String nomorSertifMandor;
}