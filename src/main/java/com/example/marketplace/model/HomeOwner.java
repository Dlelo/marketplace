package com.example.marketplace.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "home_owners")
public class HomeOwner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String nationalId;
    private String phoneNumber;
    private String email;
    private String homeLocation;
    private String houseType;
    private String numberOfRooms;
    private Integer numberOfDependents;
    private String idDocument;

    private boolean verified = false;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
