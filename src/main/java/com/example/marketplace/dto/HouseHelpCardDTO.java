package com.example.marketplace.dto;

import com.example.marketplace.enums.AvailabilityType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class HouseHelpCardDTO {
    private Long id;
    private String name;
    private AvailabilityType houseHelpType;
    private List<String> skills;
    private boolean verified;
    private boolean securityCleared;
}
