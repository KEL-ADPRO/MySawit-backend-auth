package com.mysawit.mysawit_auth.util;

import com.mysawit.mysawit_auth.model.Role;
import com.mysawit.mysawit_auth.model.User;
import com.mysawit.mysawit_auth.repository.AuthRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    private final AuthRepository authRepository;
    private final PasswordHasher passwordHasher;

    @Value("${app.seed.admin.email}")
    private String adminEmail;

    @Value("${app.seed.admin.password}")
    private String adminPassword;

    @Value("${app.seed.admin.name}")
    private String adminName;

    @Value("${app.seed.admin.username}")
    private String adminUsername;

    @Override
    @Transactional
    public void run(final String... args) {
        if (authRepository.findByEmail(adminEmail).isEmpty()) {
            User admin = User.builder()
                    .name(adminName)
                    .username(adminUsername)
                    .email(adminEmail)
                    .password(passwordHasher.hash(adminPassword))
                    .role(Role.ADMIN)
                    .build();
            authRepository.save(admin);
            log.info("=== Default admin created: {} / {} ===", adminUsername, adminEmail);
        }
    }
}