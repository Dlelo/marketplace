package com.example.marketplace.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private String phoneNumber;
    private double amount;
    private String email;
}
