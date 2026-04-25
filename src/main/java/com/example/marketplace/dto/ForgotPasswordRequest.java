package com.example.marketplace.dto;

import lombok.Data;

@Data
public class ForgotPasswordRequest {
    /** Email or phone number. */
    private String identifier;
}
