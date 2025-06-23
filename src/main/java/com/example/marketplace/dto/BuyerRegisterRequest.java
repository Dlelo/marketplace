package com.example.marketplace.dto;

import lombok.Data;

@Data
public class BuyerRegisterRequest {
    private String username;
    private String email;
    private String password;
    private String location;
    private String preferences;
    private int numberOfChildren;
    private int houseSize;
}
