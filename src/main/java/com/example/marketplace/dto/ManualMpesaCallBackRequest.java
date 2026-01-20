package com.example.marketplace.dto;

import com.example.marketplace.enums.PaymentStatus;
import lombok.Data;

@Data
public class ManualMpesaCallBackRequest {
    private String transactionId;
    private PaymentStatus status;
}
