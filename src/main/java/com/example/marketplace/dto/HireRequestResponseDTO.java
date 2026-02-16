package com.example.marketplace.dto;

import com.example.marketplace.enums.RequestStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class HireRequestResponseDTO {
    private Long id;
    private LocalDateTime createdAt;
    private boolean paid;
    private RequestStatus status;
    private LocalDate startDate;
    private String message;
    private HomeOwnerSummaryDTO homeOwner;
    private HouseHelpSummaryDTO houseHelp;
}