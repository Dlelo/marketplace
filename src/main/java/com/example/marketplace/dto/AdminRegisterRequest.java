package com.example.marketplace.dto;

import lombok.Data;

@Data
public class AdminRegisterRequest {
    private String email;

    private String password;
    private String name;
}