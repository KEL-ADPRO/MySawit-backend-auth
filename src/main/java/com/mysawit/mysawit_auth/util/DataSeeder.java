package com.mysawit.mysawit_auth.util;

import com.mysawit.mysawit_auth.model.Role;
import com.mysawit.mysawit_auth.model.User;
import com.mysawit.mysawit_auth.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (authRepository.findByEmail("admin@mysawit.com").isEmpty()) {
            User admin = new User(
                    "admin",
                    "Admin Utama",
                    "admin@mysawit.com",
                    passwordEncoder.encode("admin123"),
                    Role.ADMIN
            );
            authRepository.save(admin);
            log.info("=== Default admin created: admin@mysawit.com / admin123 ===");
        }
    }
}