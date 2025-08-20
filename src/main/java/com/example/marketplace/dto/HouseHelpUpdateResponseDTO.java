package com.example.marketplace.dto;

import com.example.marketplace.model.HouseHelp;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class HouseHelpUpdateResponseDTO {
    private HouseHelp updatedHouseHelp;
    private List<String> missingFields;
}
