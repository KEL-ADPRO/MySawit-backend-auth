package com.mysawit.mysawit_auth.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table (name = "users")
@NoArgsConstructor
@Getter @Setter
public class User {
    @Id
    @Column(unique = true, nullable = false)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(unique = true)
    private String nomorSertifMandor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public User(String username, String name, String email, String password, Role role) {
        this.id = UUID.randomUUID();
        this.username = username;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

}
