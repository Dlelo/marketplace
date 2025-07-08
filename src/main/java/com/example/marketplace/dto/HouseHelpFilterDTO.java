package com.example.marketplace.dto;

import com.example.marketplace.enums.AvailabilityType;
import lombok.Data;

@Data
public class HouseHelpFilterDTO {

    /**
     * Minimum number of years of experience required.
     */
    private Integer minExperience;

    /**
     * Expected availability type (FULL_TIME, PART_TIME, LIVE_IN).
     */
    private AvailabilityType availability;

    /**
     * Minimum acceptable salary.
     */
    private Double minSalary;

    /**
     * Maximum acceptable salary.
     */
    private Double maxSalary;

/**
 * Languages the employer prefers the house help to speak. */
}


