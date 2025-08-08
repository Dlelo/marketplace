package com.example.marketplace.dto;

import com.example.marketplace.enums.RequestStatus;
import lombok.Data;

@Data
public class HireRequestResponseDTO {
    private Long id;
    private Long houseOwnerId;
    private Long houseHelpId;
    private RequestStatus status;
}