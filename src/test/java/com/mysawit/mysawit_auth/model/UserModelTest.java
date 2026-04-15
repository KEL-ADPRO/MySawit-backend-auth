package com.mysawit.mysawit_auth.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserModelTest {

    User admin;
    User mandor;
    User buruh;
    User supir;

    @BeforeEach
    void setUp() {
        admin = User.builder()
                .id(UUID.fromString("eb558e9f-1c39-460e-8860-71af6af63bd6"))
                .username("Admin Sawit")
                .name("Agus")
                .email("admin@gmail.com")
                .password("admin123")
                .role(Role.ADMIN)
                .build();

        mandor = User.builder()
                .id(UUID.fromString("fc558e9f-1c39-460e-8860-71af6af63bd6"))
                .username("Mandor Sawit")
                .name("Burhan")
                .email("burhan@gmail.com")
                .password("mandor123")
                .role(Role.MANDOR)
                .nomorSertifMandor("CERT-001")
                .build();

        buruh = User.builder()
                .id(UUID.fromString("ab558e9f-1c39-460e-8860-71af6af63bd6"))
                .username("Buruh Sawit")
                .name("Usep")
                .email("usep@gmail.com")
                .password("buruh123")
                .role(Role.BURUH)
                .build();

        supir = User.builder()
                .id(UUID.fromString("cd558e9f-1c39-460e-8860-71af6af63bd6"))
                .username("Supir Sawit")
                .name("Budi")
                .email("budi@gmail.com")
                .password("supir123")
                .role(Role.SUPIR)
                .build();
    }

    @Test
    void adminId() {
        assertEquals(UUID.fromString("eb558e9f-1c39-460e-8860-71af6af63bd6"), admin.getId());
    }

    @Test
    void adminUsername() {
        assertEquals("Admin Sawit", admin.getUsername());
    }

    @Test
    void adminName() {
        assertEquals("Agus", admin.getName());
    }

    @Test
    void adminEmail() {
        assertEquals("admin@gmail.com", admin.getEmail());
    }

    @Test
    void adminPassword() {
        assertEquals("admin123", admin.getPassword());
    }

    @Test
    void adminRole() {
        assertEquals(Role.ADMIN, admin.getRole());
    }

    @Test
    void adminNomorSertifMandor() {
        assertNull(admin.getNomorSertifMandor());
    }

    @Test
    void mandorId() {
        assertEquals(UUID.fromString("fc558e9f-1c39-460e-8860-71af6af63bd6"), mandor.getId());
    }

    @Test
    void mandorUsername() {
        assertEquals("Mandor Sawit", mandor.getUsername());
    }

    @Test
    void mandorName() {
        assertEquals("Burhan", mandor.getName());
    }

    @Test
    void mandorEmail() {
        assertEquals("burhan@gmail.com", mandor.getEmail());
    }

    @Test
    void mandorRole() {
        assertEquals(Role.MANDOR, mandor.getRole());
    }

    @Test
    void mandorNomorSertifMandor() {
        assertEquals("CERT-001", mandor.getNomorSertifMandor());
    }

    @Test
    void buruhId() {
        assertEquals(UUID.fromString("ab558e9f-1c39-460e-8860-71af6af63bd6"), buruh.getId());
    }

    @Test
    void buruhUsername() {
        assertEquals("Buruh Sawit", buruh.getUsername());
    }

    @Test
    void buruhName() {
        assertEquals("Usep", buruh.getName());
    }

    @Test
    void buruhEmail() {
        assertEquals("usep@gmail.com", buruh.getEmail());
    }

    @Test
    void buruhRole() {
        assertEquals(Role.BURUH, buruh.getRole());
    }

    @Test
    void buruhNomorSertifMandor() {
        assertNull(buruh.getNomorSertifMandor());
    }

    @Test
    void supirId() {
        assertEquals(UUID.fromString("cd558e9f-1c39-460e-8860-71af6af63bd6"), supir.getId());
    }

    @Test
    void supirUsername() {
        assertEquals("Supir Sawit", supir.getUsername());
    }

    @Test
    void supirName() {
        assertEquals("Budi", supir.getName());
    }

    @Test
    void supirEmail() {
        assertEquals("budi@gmail.com", supir.getEmail());
    }

    @Test
    void supirRole() {
        assertEquals(Role.SUPIR, supir.getRole());
    }

    @Test
    void supirNomorSertifMandor() {
        assertNull(supir.getNomorSertifMandor());
    }

    @Test
    void noArgsConstructor() {
        final User user = new User();

        assertNull(user.getId());
        assertNull(user.getUsername());
        assertNull(user.getName());
        assertNull(user.getEmail());
        assertNull(user.getPassword());
        assertNull(user.getRole());
        assertNull(user.getNomorSertifMandor());
    }

    @Test
    void setUsernameTest() {
        admin.setUsername("atmin");
        assertEquals("atmin", admin.getUsername());
    }

    @Test
    void setNameTest() {
        admin.setName("cecep");
        assertEquals("cecep", admin.getName());
    }

    @Test
    void setEmailTest() {
        admin.setEmail("adminbaru@gmail.com");
        assertEquals("adminbaru@gmail.com", admin.getEmail());
    }

    @Test
    void setPasswordTest() {
        admin.setPassword("newpassword");
        assertEquals("newpassword", admin.getPassword());
    }

    @Test
    void setRoleTest() {
        admin.setRole(Role.MANDOR);
        assertEquals(Role.MANDOR, admin.getRole());
    }

    @Test
    void setNomorSertifMandorTest() {
        mandor.setNomorSertifMandor("CERT-999");
        assertEquals("CERT-999", mandor.getNomorSertifMandor());
    }

    @Test
    void setIdTest() {
        UUID newId = UUID.randomUUID();
        admin.setId(newId);
        assertEquals(newId, admin.getId());
    }

    @Test
    void roleAdminExists() {
        assertEquals(Role.ADMIN, Role.valueOf("ADMIN"));
    }

    @Test
    void roleMandorExists() {
        assertEquals(Role.MANDOR, Role.valueOf("MANDOR"));
    }

    @Test
    void roleBuruhExists() {
        assertEquals(Role.BURUH, Role.valueOf("BURUH"));
    }

    @Test
    void roleSupirExists() {
        assertEquals(Role.SUPIR, Role.valueOf("SUPIR"));
    }

    @Test
    void roleInvalidValue() {
        assertThrows(IllegalArgumentException.class, () -> Role.valueOf("INVALID_ROLE"));
    }
}