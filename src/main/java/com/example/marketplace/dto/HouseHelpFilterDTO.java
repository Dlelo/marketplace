package com.example.marketplace.dto;

import com.example.marketplace.enums.AvailabilityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HouseHelpFilterDTO {

    private Integer experience;

    // Availability type (e.g., FULL_TIME, PART_TIME, LIVE_IN, LIVE_OUT)
    private AvailabilityType availability;

    // Salary range
    private Double minExpectedSalary;
    private Double maxExpectedSalary;

    // Optional: filter by earliest start date
    private LocalDate availableFrom;

    private String languages;

    // Optional: location-based filtering
    private String location;

    private Integer numberOfChildren;
}


