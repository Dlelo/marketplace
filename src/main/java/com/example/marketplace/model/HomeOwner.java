package com.example.marketplace.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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


    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private boolean verified = false;
    private boolean subscriptionActive;

    private LocalDate subscriptionExpiryDate;

    @OneToMany(mappedBy = "homeOwner", cascade = CascadeType.ALL)
    private List<HireRequest> hireRequests = new ArrayList<>();
}
