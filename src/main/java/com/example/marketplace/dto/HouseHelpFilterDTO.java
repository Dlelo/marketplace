package com.example.marketplace.dto;

import com.example.marketplace.enums.AvailabilityType;
import lombok.Data;

@Data
public class HouseHelpFilterDTO {

    private Integer minExperience;
    private AvailabilityType availability;
    private Double minSalary;
    private Double maxSalary;
}


