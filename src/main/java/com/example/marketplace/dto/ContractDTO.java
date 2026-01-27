package com.example.marketplace.dto;

import com.example.marketplace.enums.ContractStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

@Data
public class ContractDTO {

    private Long id;

    @NotNull(message = "House help ID is required")
    private Long houseHelpId;

    @NotNull(message = "Home owner ID is required")
    private Long homeOwnerId;

    @NotNull(message = "Start date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private ContractStatus status;

    @Positive(message = "Agreed rate must be positive")
    private Double agreedRate;

    private String terms;

    private String notes;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdAt;

    private String houseHelpName;
    private String homeOwnerName;
}
