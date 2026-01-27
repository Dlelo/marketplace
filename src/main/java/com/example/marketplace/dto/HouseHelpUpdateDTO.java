package com.example.marketplace.dto;

import com.example.marketplace.enums.AvailabilityType;
import lombok.Data;

import java.util.List;

@Data
public class HouseHelpUpdateDTO {
    private Integer numberOfChildren;
    private AvailabilityType houseHelpType;
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
    private String height;
    private String weight;
    private String age;
    private String gender;
    private String localAuthorityVerificationDocument;
    private HouseHelpPreferenceUpdateDTO preferences;
    private GeoLocationUpdateDTO pinLocation;
    private Integer maxTravelDistanceKm;
}
