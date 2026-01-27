package com.example.marketplace.dto;

import com.example.marketplace.enums.*;
import lombok.Data;

import java.util.List;

@Data
public class HomeOwnerPreferenceUpdateDTO {

    private AvailabilityType houseHelpType;
    private Integer minExperience;
    private String location;

    private List<String> preferredSkills;
    private List<String> preferredLanguages;

    private Integer minMatchScore;

    private List<ChildAgeRange> childrenAgeRanges;
    private Integer numberOfChildren;

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
