package com.example.marketplace.dto;

import com.example.marketplace.model.HouseHelp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HouseHelpVerificationResponseDTO {
    private Long id;
    private boolean verified;
    private List<String> missingFields;

    public static HouseHelpVerificationResponseDTO fromEntity(HouseHelp houseHelp, List<String> missingFields) {
        return new HouseHelpVerificationResponseDTO(
                houseHelp.getId(),
                houseHelp.isVerified(),
                missingFields
        );
    }
}
