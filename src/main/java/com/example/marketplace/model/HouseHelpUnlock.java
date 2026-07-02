package com.example.marketplace.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "house_help_unlocks",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "house_help_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HouseHelpUnlock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "house_help_id", nullable = false)
    private HouseHelp houseHelp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @Column(nullable = false)
    private LocalDateTime unlockedAt = LocalDateTime.now();
}
