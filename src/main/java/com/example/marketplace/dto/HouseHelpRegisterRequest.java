package com.example.marketplace.dto;

import lombok.Data;

import java.util.List;

@Data
public class HouseHelpRegisterRequest {
    private String username;
    private String email;
    private String password;
    private String name;
    private int experienceYears;
    private List<String> skills;
    private String idNumber;
    private String numberOfChildren;
    private List<String> types;
    private String availability;
    private String bio;
    private Double expectedSalary;
    private String photoUrl;
}