package com.example.marketplace.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "agent")
public class Agent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String nationalId;
    private String phoneNumber;
    private String email;
    private String locationOfOperation;
    private String homeLocation;
    private String houseNumber;
    private String idDocument;

    private boolean verified = false;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
