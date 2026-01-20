package com.example.marketplace.model;

import com.example.marketplace.enums.AvailabilityType;
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
    private List<String> preferredSkills;

    @ElementCollection
    private List<String> preferredLanguages;
}
