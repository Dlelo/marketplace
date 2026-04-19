package com.example.marketplace.dto;

import com.example.marketplace.enums.RequestStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AgentHireRequestDTO {
    private Long id;
    private String houseHelpName;
    private Long houseHelpUserId;
    private String homeOwnerName;
    private Long homeOwnerUserId;
    private Long homeOwnerId;
    private RequestStatus status;
    private LocalDateTime createdAt;
    private LocalDate startDate;
    private double commissionEarned;
}
