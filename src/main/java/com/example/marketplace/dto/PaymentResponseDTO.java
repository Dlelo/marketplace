package com.example.marketplace.dto;

import lombok.Data;
import java.time.LocalDateTime;

import com.example.marketplace.enums.PaymentStatus;
import com.example.marketplace.model.User;


@Data
public class PaymentResponseDTO {
    private Long id;
    private String transactionId;
    private Double amount;
    private String provider; // M-PESA
    private PaymentStatus status;
    private LocalDateTime createdAt;
    private Long userId;
    private String userEmail;
    private  String userName;
}
