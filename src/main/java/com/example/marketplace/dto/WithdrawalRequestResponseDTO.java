package com.example.marketplace.dto;

import com.example.marketplace.enums.WithdrawalStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WithdrawalRequestResponseDTO {
    private Long id;
    private Double amount;
    private WithdrawalStatus status;
    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;
    private String mpesaPhone;
    private String notes;
}
