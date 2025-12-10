package com.example.marketplace.dto;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String name;
    private String email;
    private String phoneNumber;
    private String role;

    // Optional: HomeOwner fields
    private Integer numberOfDependents;
    private String houseType;
    private String numberOfRooms;
    private String homeLocation;

    // Optional: HouseHelp fields
    private Integer yearsOfExperience;
    private String skills;
    private Boolean verified;
}
