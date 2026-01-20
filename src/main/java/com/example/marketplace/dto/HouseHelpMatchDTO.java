package com.example.marketplace.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HouseHelpMatchDTO {
    private HouseHelpCardDTO houseHelp;
    private int matchScore;
}
