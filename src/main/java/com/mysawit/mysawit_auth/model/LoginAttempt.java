package com.mysawit.mysawit_auth.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "login_attempts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginAttempt {
    @Id
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private int failedCount;

    @Column(nullable = false)
    private Instant lastFailedAt;

    @Column
    private Instant lockedUntil;
}