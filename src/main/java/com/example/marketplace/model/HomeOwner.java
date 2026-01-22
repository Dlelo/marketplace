package com.example.marketplace.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "home_owners")
@JsonInclude(JsonInclude.Include.ALWAYS)
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
    private String nationalIdDocument;
    private String profilePictureDocument;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"homeOwner"})
    private User user;

    private boolean verified = false;
    private boolean subscriptionActive;

    private LocalDate subscriptionExpiryDate;

    @OneToMany(mappedBy = "homeOwner", cascade = CascadeType.ALL)
    private List<HireRequest> hireRequests = new ArrayList<>();

    private boolean active = true;
    private boolean securityCleared = false;
    private String securityClearanceComments;

    @Embedded
    private HomeOwnerPreference preferences;

}
