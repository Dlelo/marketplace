package com.example.marketplace.dto;

import com.example.marketplace.enums.AvailabilityType;
import com.example.marketplace.enums.Skills;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class HouseHelpCardDTO {
    private Long id;
    private String name;
    private AvailabilityType houseHelpType;
    private List<Skills> skills;
    private boolean verified;
    private boolean securityCleared;
}
