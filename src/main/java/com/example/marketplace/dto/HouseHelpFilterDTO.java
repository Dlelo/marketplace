package com.example.marketplace.dto;

import com.example.marketplace.enums.AvailabilityType;
import com.example.marketplace.enums.HouseHelpStatus;
import com.example.marketplace.enums.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HouseHelpFilterDTO {

    private Integer experience;

    private Boolean active;

    private HouseHelpStatus status;

    private AvailabilityType houseHelpType;

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


