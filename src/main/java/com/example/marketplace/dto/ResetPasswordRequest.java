package com.example.marketplace.dto;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    /** Session token returned by /forgot-password. */
    private String token;
    /** 6-digit OTP delivered via SMS or email. */
    private String code;
    private String newPassword;
}
