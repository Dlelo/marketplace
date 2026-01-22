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
public class HomeOwnerFilterDTO {
    private Boolean active;
    private String houseType;
    private String numberOfRooms;
    private String homeLocation;
    private Integer numberOfDependents;
}


