package com.example.marketplace.dto;

import com.example.marketplace.enums.Skills;
import lombok.Data;

import java.util.List;

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
    private List<Skills> skills;
    private Boolean verified;
}
