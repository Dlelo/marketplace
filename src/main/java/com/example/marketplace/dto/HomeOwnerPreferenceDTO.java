package com.example.marketplace.dto;

import com.example.marketplace.enums.AvailabilityType;

import java.util.List;

public record HomeOwnerPreferenceDTO(
        AvailabilityType houseHelpType,
        Integer minExperience,
        String location,
        List<String> preferredSkills,
        List<String> preferredLanguages,
        Integer minMatchScore
) {}