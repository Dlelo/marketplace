package com.example.marketplace.dto;

import lombok.Data;

@Data
public class RegisterRequest {

    private String email;

    private String phoneNumber;

    private String password;

    private String name;
}
