package com.mysawit.mysawit_auth.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "blacklisted_tokens")
@Getter @Setter
@Builder @NoArgsConstructor
@AllArgsConstructor
public class BlacklistedToken {

    @Id
    @Column(length = 512)
    private String token;

    @Column(nullable = false)
    private Instant expiresAt;
}