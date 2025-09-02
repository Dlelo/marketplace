package com.example.marketplace.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "subscriptions")
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String plan; // e.g., BASIC, PREMIUM
    private Double amount;
    private boolean active;
    private LocalDate startDate;
    private LocalDate endDate;

    @ManyToOne
    private User user;
}
