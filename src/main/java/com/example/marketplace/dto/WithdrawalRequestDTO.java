package com.example.marketplace.dto;

import lombok.Data;

@Data
public class WithdrawalRequestDTO {
    private Double amount;
    private String mpesaPhone;
    private String notes;
}
