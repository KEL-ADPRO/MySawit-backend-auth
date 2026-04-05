package com.mysawit.mysawit_auth.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordHasher {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public String hash(final String rawPassword) {
        return encoder.encode(rawPassword);
    }

    public boolean matches(final String rawPassword, final String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}