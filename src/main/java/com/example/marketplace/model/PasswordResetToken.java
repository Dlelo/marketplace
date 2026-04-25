package com.example.marketplace.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Data
@Entity
@Table(name = "password_reset_tokens", indexes = {
        @Index(name = "idx_prt_token", columnList = "token", unique = true),
        @Index(name = "idx_prt_user", columnList = "user_id")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Opaque session handle returned to the client; ties subsequent calls back to this user. */
    @Column(nullable = false, unique = true, length = 64)
    private String token;

    /** BCrypt hash of the 6-digit OTP delivered to the user. Never store the plain OTP. */
    @Column(name = "code_hash", nullable = false, length = 100)
    private String codeHash;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Instant expiresAt;

    private Instant usedAt;

    @Column(nullable = false)
    private Instant createdAt;

    /** Number of failed code attempts. Locked once it crosses a threshold. */
    @Column(nullable = false)
    private int attempts;

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isUsed() {
        return usedAt != null;
    }

    public boolean isValid() {
        return !isExpired() && !isUsed();
    }
}
