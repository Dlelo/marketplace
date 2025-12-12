package com.example.marketplace.dto;

import com.example.marketplace.enums.PaymentStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentStatusDTO {
    private Long id;
    private String transactionId;
    private Double amount;
    private String provider; // M-PESA
    private PaymentStatus status;
    private LocalDateTime createdAt;
    private Long userId;
    private String userEmail;
}

