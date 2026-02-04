package com.example.marketplace.model;

import com.example.marketplace.enums.*;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Embeddable
@Data
public class HouseHelpPreference {

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_house_help_type")
    private AvailabilityType houseHelpType;

    private Integer minExperience;

    private String preferredLocation;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<Skills> preferredSkills;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<Languages> preferredLanguages;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<ChildAgeRange> preferredChildAgeRanges;

    private Integer preferredMaxChildren;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<CareService> preferredServices;

    private String preferredReligion;

    private Boolean okayWithPets;

    private Double minSalary;
    private Double maxSalary;
}
