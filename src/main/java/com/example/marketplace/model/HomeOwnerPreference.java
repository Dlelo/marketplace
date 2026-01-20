package com.example.marketplace.model;

import com.example.marketplace.enums.AvailabilityType;
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
    private List<String> preferredSkills;

    @ElementCollection
    private List<String> preferredLanguages;

    private Integer minMatchScore = 50;
}
