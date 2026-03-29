package com.example.marketplace.dto;

import com.example.marketplace.enums.AvailabilityType;
import lombok.Data;

import java.util.List;

@Data
public class HouseHelpListDTO {
    private Long id;
    private boolean active;
    private boolean verified;
    private boolean securityCleared;
    private String profilePictureDocument;
    private String currentLocation;
    private Integer yearsOfExperience;
    private AvailabilityType houseHelpType;
    private List<String> skills;
    private UserSummary user;

    @Data
    public static class UserSummary {
        private Long id;
        private String name;
        private String email;
        private String phoneNumber;
    }
}
