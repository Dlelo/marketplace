package com.example.marketplace.dto;

import com.example.marketplace.enums.AvailabilityType;
import com.example.marketplace.enums.CountyOptions;
import lombok.Data;

import java.util.List;

@Data
public class HouseHelpResponseDTO {
    private Long id;
    private Integer numberOfChildren;
    private List<String> languages;
    private String levelOfEducation;
    private String contactPersons;
    private String homeLocation;
    private CountyOptions homeCounty;
    private String currentLocation;
    private CountyOptions currentCounty;
    private String nationalId;
    private String nationalIdDocument;
    private String profilePictureDocument;
    private String medicalReport;
    private String goodConduct;
    private Integer yearsOfExperience;
    private String religion;
    private List<String> skills;
    private String height;
    private String weight;
    private String age;
    private String gender;
    private String contactPersonsPhoneNumber;
    private AvailabilityType houseHelpType;
}
