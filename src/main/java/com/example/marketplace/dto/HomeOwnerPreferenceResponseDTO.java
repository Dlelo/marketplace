package com.example.marketplace.dto;

import com.example.marketplace.enums.*;
import lombok.Data;

import java.util.List;

@Data
public class HomeOwnerPreferenceResponseDTO {

    private AvailabilityType houseHelpType;
    private Integer minExperience;
    private String location;

    private List<Skills> preferredSkills;
    private List<Languages> preferredLanguages;

    private Integer minMatchScore;

    private List<ChildAgeRange> childrenAgeRanges;
    private Integer numberOfChildren;

    private List<CareService> requiredServices;

    private Boolean hasPets;
    private String religionPreference;
    private Boolean requiresSecurityCleared;

    private Integer preferredMinAge;
    private Integer preferredMaxAge;

    private Double preferredMinSalary;
    private Double preferredMaxSalary;
}
