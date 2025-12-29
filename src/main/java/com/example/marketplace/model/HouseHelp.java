package com.example.marketplace.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Entity
@Table(name = "house_help")
@JsonInclude(JsonInclude.Include.ALWAYS)
public class HouseHelp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonIgnoreProperties({"houseHelp", "homeOwner", "roles"})
    private User user;

    @Column(nullable = false)
    private boolean verified;

    private Integer numberOfChildren;

    @ElementCollection
    private List<String> languages;

    private String levelOfEducation;
    private String contactPersons;
    private String homeLocation;
    private String currentLocation;
    private String nationalId;
    private String nationalIdDocument;
    private String contactPersonsPhoneNumber;
    private String medicalReport;
    private String goodConduct;
    private Integer yearsOfExperience;
    private String religion;
    private String height;
    private String weight;
    private String age;
    private String gender;
    private String localAuthorityVerificationDocument;
    @ElementCollection
    private List<String> skills;

    private boolean active = true;
}
