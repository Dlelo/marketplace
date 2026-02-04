package com.example.marketplace.model;

import com.example.marketplace.enums.*;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.util.List;

@Embeddable
@Data
public class HomeOwnerPreference {

    @Enumerated(EnumType.STRING)
    private AvailabilityType houseHelpType;

    private Integer minExperience;

    private String location;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<Skills> preferredSkills;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<Languages> preferredLanguages;

    private Integer minMatchScore = 50;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<ChildAgeRange> childrenAgeRanges;

    private Integer numberOfChildren;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<CareService> requiredServices;

    private Boolean hasPets;

    private String religionPreference;

    private Boolean requiresSecurityCleared;

    private Integer preferredMaxAge;
    private Integer preferredMinAge;

    private Double minSalary;
    private Double maxSalary;

    private Double preferredMaxSalary;
    private Double preferredMinSalary;
}
