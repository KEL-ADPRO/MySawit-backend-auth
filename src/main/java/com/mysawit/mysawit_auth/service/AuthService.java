package com.mysawit.mysawit_auth.service;

import com.mysawit.mysawit_auth.util.JwtUtil;
import com.mysawit.mysawit_auth.util.AuthResponse;
import com.mysawit.mysawit_auth.util.LoginRequest;
import com.mysawit.mysawit_auth.util.RegisterRequest;
import com.mysawit.mysawit_auth.model.Role;
import com.mysawit.mysawit_auth.model.User;
import com.mysawit.mysawit_auth.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already in use");
        }
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username is already taken");
        }
        if (request.getRole() == Role.ADMIN) {
            throw new IllegalArgumentException("Cannot self-register as Admin");
        }
        if (request.getRole() == Role.MANDOR) {
            if (request.getNomorSertifMandor() == null || request.getNomorSertifMandor().isBlank()) {
                throw new IllegalArgumentException("Nomor Sertifikasi Mandor is required for Mandor role");
            }
        }

        User user = new User(
                request.getUsername(),
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getRole()
        );

        if (request.getRole() == Role.MANDOR) {
            user.setNomorSertifMandor(request.getNomorSertifMandor());
        }

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token, user.getId(), user.getUsername(),
                user.getName(), user.getEmail(), user.getRole());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token, user.getId(), user.getUsername(),
                user.getName(), user.getEmail(), user.getRole());
    }

    public AuthResponse getUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token, user.getId(), user.getUsername(),
                user.getName(), user.getEmail(), user.getRole());
    }
}