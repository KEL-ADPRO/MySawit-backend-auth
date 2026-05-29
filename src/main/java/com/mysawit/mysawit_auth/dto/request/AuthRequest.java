package com.mysawit.mysawit_auth.dto.request;

import com.mysawit.mysawit_auth.model.Role;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {
    // Password login
    @Email(message = "Invalid email format")
    private String email;
    private String password;

    // Google login
    private String idToken;
    private String username;
    private Role role;
    private String nomorSertifMandor;
}
