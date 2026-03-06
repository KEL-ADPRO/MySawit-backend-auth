package com.mysawit.mysawit_auth.util;

import com.mysawit.mysawit_auth.model.Role;
import com.mysawit.mysawit_auth.model.User;
import com.mysawit.mysawit_auth.repository.AuthRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    @Autowired
    private final AuthRepository authRepository;
    private final PasswordHasher passwordHasher;

    @Override
    @Transactional
    public void run(String... args) {
        if (authRepository.findByEmail("admin.MySawit19@gmail.com") == null) {
            User admin = new User();
            admin.setName("Admin MySawit Kel.19");
            admin.setUsername("Admin Utama");
            admin.setEmail("admin.MySawit19@gmail.com");
            admin.setPassword(passwordHasher.hash("adminKel19"));
            admin.setRole(Role.ADMIN);
            authRepository.save(admin);
            log.info("=== Default admin created: admin.MySawit19@gmail.com / adminKel19 ===");
        }
    }
}