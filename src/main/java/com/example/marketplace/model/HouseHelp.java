package com.example.marketplace.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class HouseHelp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String bio;
    private Boolean verified;
    private Integer experienceYears;
    private Double expectedSalary;
    private String languages;
    private String photoUrl;
    private String videoUrl;
    private String availability;

    @ElementCollection
    private List<String> skills;
    private String numberOfChildren;
    private String idNumber;
}