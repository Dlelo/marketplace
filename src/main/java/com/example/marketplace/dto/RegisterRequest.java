package com.example.marketplace.dto;

import lombok.Data;

@Data
public class RegisterRequest {

    private String email;

    private String username;

    private String password;

    private String name;
}
