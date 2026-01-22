package com.example.marketplace.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SecurityClearanceRequest {

    private boolean cleared;

    @Size(max = 1000, message = "Comments must be 1000 characters or less")
    private String comments;
}