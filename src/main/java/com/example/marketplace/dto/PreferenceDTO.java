package com.example.marketplace.dto;

import com.example.marketplace.enums.AvailabilityType;
import lombok.Data;

import java.util.List;

@Data
public class PreferenceDTO {
    private AvailabilityType houseHelpType;
    private Integer minExperience;
    private String location;
    private List<String> preferredSkills;
    private List<String> preferredLanguages;
}
