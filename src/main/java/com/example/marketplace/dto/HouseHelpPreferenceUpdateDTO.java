package com.example.marketplace.dto;

import com.example.marketplace.enums.*;
import lombok.Data;

import java.util.List;

@Data
public class HouseHelpPreferenceUpdateDTO {

    private AvailabilityType houseHelpType;
    private Integer minExperience;
    private String preferredLocation;

    private List<String> preferredSkills;
    private List<String> preferredLanguages;

    private List<ChildAgeRange> preferredChildAgeRanges;
    private Integer preferredMaxChildren;

    private List<CareService> preferredServices;

    private String preferredReligion;
    private Boolean okayWithPets;

    private Double minSalary;
    private Double maxSalary;
}
