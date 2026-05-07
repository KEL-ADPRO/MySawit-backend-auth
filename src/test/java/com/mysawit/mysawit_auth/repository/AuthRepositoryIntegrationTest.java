package com.mysawit.mysawit_auth.repository;

import com.mysawit.mysawit_auth.model.Role;
import com.mysawit.mysawit_auth.model.User;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(AuthRepositoryImpl.class)
@Transactional
public class AuthRepositoryIntegrationTest {

    @Autowired
    private AuthRepositoryImpl authRepository;

    @Autowired
    private EntityManager entityManager;

    private User admin;
    private User mandor;
    private User buruh;
    private User supir;

    @BeforeEach
    void setUp() {
        admin = User.builder()
                .username("Admin Sawit")
                .name("Agus")
                .email("admin@gmail.com")
                .password("hashed_admin123")
                .role(Role.ADMIN)
                .build();

        mandor = User.builder()
                .username("Mandor Sawit")
                .name("Burhan")
                .email("burhan@gmail.com")
                .password("hashed_mandor123")
                .role(Role.MANDOR)
                .nomorSertifMandor("CERT-001")
                .build();

        buruh = User.builder()
                .username("Buruh Sawit")
                .name("Usep")
                .email("usep@gmail.com")
                .password("hashed_buruh123")
                .role(Role.BURUH)
                .build();

        supir = User.builder()
                .username("Supir Sawit")
                .name("Budi")
                .email("budi@gmail.com")
                .password("hashed_supir123")
                .role(Role.SUPIR)
                .build();
    }

    @Test
    void saveAdminReturnsPersistedUser() {
        final User saved = authRepository.save(admin);
        entityManager.flush();

        assertNotNull(saved.getId());
        assertEquals("admin@gmail.com", saved.getEmail());
        assertEquals(Role.ADMIN, saved.getRole());
    }

    @Test
    void saveMandorPersistsCertification() {
        authRepository.save(mandor);
        entityManager.flush();
        entityManager.clear();

        final User found = authRepository.findByEmail("burhan@gmail.com");
        assertNotNull(found);
        assertEquals("CERT-001", found.getNomorSertifMandor());
    }

    @Test
    void saveBuruhNoCertification() {
        authRepository.save(buruh);
        entityManager.flush();
        entityManager.clear();

        final User found = authRepository.findByEmail("usep@gmail.com");
        assertNotNull(found);
        assertNull(found.getNomorSertifMandor());
    }

    @Test
    void saveSupirNoCertification() {
        authRepository.save(supir);
        entityManager.flush();
        entityManager.clear();

        final User found = authRepository.findByEmail("budi@gmail.com");
        assertNotNull(found);
        assertNull(found.getNomorSertifMandor());
    }

    @Test
    void findByEmail_AdminFound() {
        authRepository.save(admin);
        entityManager.flush();
        entityManager.clear();

        final User found = authRepository.findByEmail("admin@gmail.com");

        assertNotNull(found);
        assertEquals("admin@gmail.com", found.getEmail());
        assertEquals("Admin Sawit", found.getUsername());
        assertEquals(Role.ADMIN, found.getRole());
    }

    @Test
    void findByEmail_MandorFound() {
        authRepository.save(mandor);
        entityManager.flush();
        entityManager.clear();

        final User found = authRepository.findByEmail("burhan@gmail.com");

        assertNotNull(found);
        assertEquals(Role.MANDOR, found.getRole());
        assertEquals("CERT-001", found.getNomorSertifMandor());
    }

    @Test
    void findByEmail_NotFound() {
        final User found = authRepository.findByEmail("ghost@gmail.com");
        assertNull(found);
    }

    @Test
    void findById_AdminFound() {
        final User saved = authRepository.save(admin);
        entityManager.flush();
        entityManager.clear();

        final User found = authRepository.findById(saved.getId());

        assertNotNull(found);
        assertEquals(saved.getId(), found.getId());
        assertEquals(Role.ADMIN, found.getRole());
    }

    @Test
    void findById_MandorFound() {
        final User saved = authRepository.save(mandor);
        entityManager.flush();
        entityManager.clear();

        final User found = authRepository.findById(saved.getId());

        assertNotNull(found);
        assertEquals("CERT-001", found.getNomorSertifMandor());
    }

    @Test
    void findById_NotFound() {
        final User found = authRepository.findById(UUID.randomUUID());
        assertNull(found);
    }

    @Test
    void findByUsername_AdminFound() {
        authRepository.save(admin);
        entityManager.flush();
        entityManager.clear();

        final User found = authRepository.findByUsername("Admin Sawit");

        assertNotNull(found);
        assertEquals("Admin Sawit", found.getUsername());
        assertEquals(Role.ADMIN, found.getRole());
    }

    @Test
    void findByUsername_BuruhFound() {
        authRepository.save(buruh);
        entityManager.flush();
        entityManager.clear();

        final User found = authRepository.findByUsername("Buruh Sawit");

        assertNotNull(found);
        assertEquals("usep@gmail.com", found.getEmail());
    }

    @Test
    void findByUsername_NotFound() {
        final User found = authRepository.findByUsername("Ghost User");
        assertNull(found);
    }

    @Test
    void findByGoogleId_Found() {
        final User googleUser = User.builder()
                .username("googleuser")
                .name("Google User")
                .email("google@gmail.com")
                .googleId("google-id-12345")
                .role(Role.BURUH)
                .build();
        authRepository.save(googleUser);
        entityManager.flush();
        entityManager.clear();

        final User found = authRepository.findByGoogleId("google-id-12345");

        assertNotNull(found);
        assertEquals("google-id-12345", found.getGoogleId());
    }

    @Test
    void findByGoogleId_NotFound() {
        assertNull(authRepository.findByGoogleId("nonexistent-google-id"));
    }
}