package com.example.marketplace.model;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class HouseHelp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private User user;

    private String bio;
    private Boolean verified;
    private Integer experienceYears;
    private Double expectedSalary;
    private String languages;
    private String photoUrl;
    private String videoUrl;
    private List<String> skills;

    // other fields and relationships (e.g., reviews)
}
