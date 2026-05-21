package com.mysawit.mysawit_auth.mapper;

import com.mysawit.mysawit_auth.dto.response.AuthResponse;
import com.mysawit.mysawit_auth.model.Role;
import com.mysawit.mysawit_auth.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AuthResponseMapperTest {
    private AuthResponseMapper mapper;
    private User user;
    private final UUID USER_ID = UUID.fromString("eb558e9f-1c39-460e-8860-71af6af63bd6");

    @BeforeEach
    void setUp() {
        mapper = new AuthResponseMapper();

        user = User.builder()
                .id(USER_ID)
                .username("Admin Sawit")
                .name("Agus")
                .email("admin@gmail.com")
                .password("hashed_password")
                .role(Role.ADMIN)
                .build();
    }

    @Test
    void mapsUserIdCorrectly() {
        final AuthResponse response = mapper.toResponse(user, "token");
        assertEquals(USER_ID, response.getUserId());
    }

    @Test
    void mapsUsernameCorrectly() {
        final AuthResponse response = mapper.toResponse(user, "token");
        assertEquals("Admin Sawit", response.getUsername());
    }

    @Test
    void mapsNameCorrectly() {
        final AuthResponse response = mapper.toResponse(user, "token");
        assertEquals("Agus", response.getName());
    }

    @Test
    void mapsEmailCorrectly() {
        final AuthResponse response = mapper.toResponse(user, "token");
        assertEquals("admin@gmail.com", response.getEmail());
    }

    @Test
    void mapsRoleCorrectly() {
        final AuthResponse response = mapper.toResponse(user, "token");
        assertEquals(Role.ADMIN, response.getRole());
    }

    @Test
    void mapsTokenCorrectly() {
        final AuthResponse response = mapper.toResponse(user, "dummy.jwt.token");
        assertEquals("dummy.jwt.token", response.getToken());
    }

    @Test
    void nullTokenIsAllowed() {
        final AuthResponse response = mapper.toResponse(user, null);
        assertNull(response.getToken());
    }

    @Test
    void mapsNomorSertifIfPresent() {
        final User mandor = User.builder()
                .id(UUID.fromString("fc558e9f-1c39-460e-8860-71af6af63bd6"))
                .username("Mandor Sawit")
                .name("Burhan")
                .email("burhan@gmail.com")
                .role(Role.MANDOR)
                .nomorSertifMandor("CERT-001")
                .build();

        final AuthResponse response = mapper.toResponse(mandor, "token");
        assertEquals("CERT-001", response.getNomorSertifMandor());
    }

    @Test
    void nullNomorSertifIfItsAbsent() {
        final AuthResponse response = mapper.toResponse(user, "token");
        assertNull(response.getNomorSertifMandor());
    }

    @Test
    void doesntExposePassword() {
        final AuthResponse response = mapper.toResponse(user, "token");
        assertNotEquals("hashed_password", response.getToken());
        assertNotEquals("hashed_password", response.getUsername());
        assertNotEquals("hashed_password", response.getName());
        assertNotEquals("hashed_password", response.getEmail());
    }
}