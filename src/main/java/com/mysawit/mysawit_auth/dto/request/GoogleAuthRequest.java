package com.mysawit.mysawit_auth.dto.request;

import com.mysawit.mysawit_auth.model.Role;
import jakarta.validation.constraints.NotBlank;
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

    /**
     * Opsional — hanya diperlukan saat mendaftarkan akun Google baru.
     * Untuk user yang sudah terdaftar, role diabaikan (diambil dari database).
     */
    private String username;
    private Role role;
    private String nomorSertifMandor;
}