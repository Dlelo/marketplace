package com.example.marketplace.dto;

import com.example.marketplace.enums.*;
import lombok.Data;

import java.util.List;

@Data
public class HouseHelpResponseDTO {
    private Long id;
    private boolean verified;
    private boolean active;
    private boolean securityCleared;
    private String securityClearanceComments;
    private Integer numberOfChildren;
    private List<Languages> languages;
    private String levelOfEducation;
    private String contactPersons;
    private String homeLocation;
    private CountyOptions homeCounty;
    private String currentLocation;
    private CountyOptions currentCounty;
    private String nationalId;
    private String nationalIdDocument;
    private String profilePictureDocument;
    private String contactPersonsPhoneNumber;
    private String medicalReport;
    private String goodConduct;
    private Integer yearsOfExperience;
    private String religion;
    private String height;
    private String weight;
    private String age;
    private String gender;
    private String localAuthorityVerificationDocument;
    private AvailabilityType houseHelpType;
    private String availability;
    private String experienceSummary;
    private List<ChildAgeRange> childAgeRanges;
    private List<CareService> services;
    private Integer maxChildren;
    private List<Skills> skills;
    private Integer maxTravelDistanceKm;
    private HiringStatus hiringStatus;
    private GeoLocationResponseDTO pinLocation;
    private HouseHelpPreferenceResponseDTO preferences;

    // Flattened user info
    private UserSummaryDTO user;

    @Data
    public static class UserSummaryDTO {
        private Long id;
        private String name;
        private String email;
        private String phoneNumber;
    }
}
