package com.example.marketplace.dto;

import lombok.Data;

import java.util.List;

@Data
public class HouseHelpUpdateDTO {
    private Integer numberOfChildren;
    private List<String> languages;
    private String levelOfEducation;
    private String contactPersons;
    private String contactPersonsPhoneNumber;
    private String homeLocation;
    private String currentLocation;
    private String nationalId;
    private String medicalReport;
    private String goodConduct;
    private Integer yearsOfExperience;
    private String religion;
    private List<String> skills;
    private String nationalIdDocument;
}
