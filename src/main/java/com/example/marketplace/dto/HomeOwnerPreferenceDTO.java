package com.example.marketplace.dto;

import com.example.marketplace.enums.AvailabilityType;
import com.example.marketplace.enums.CareService;
import com.example.marketplace.enums.ChildAgeRange;

import java.util.List;

public record HomeOwnerPreferenceDTO(
        AvailabilityType houseHelpType,
        Integer minExperience,
        String location,
        List<String> preferredSkills,
        List<String> preferredLanguages,
        Integer minMatchScore,
        List<ChildAgeRange> childrenAgeRanges,
        Integer numberOfChildren,
        List<CareService> requiredServices,
        Boolean hasPets,
        String religionPreference,
        Boolean requiresSecurityCleared,
        Integer preferredMinAge,
        Integer preferredMaxAge,
        Double preferredMinSalary,
        Double preferredMaxSalary
) {}