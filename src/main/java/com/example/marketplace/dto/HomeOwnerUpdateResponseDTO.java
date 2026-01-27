package com.example.marketplace.dto;

import com.example.marketplace.model.HomeOwner;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class HomeOwnerUpdateResponseDTO {
    private Long id;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String homeLocation;
    private String houseType;
    private String numberOfRooms;
    private Integer numberOfDependents;

    private GeoLocationResponseDTO pinLocation;
    private Integer maxDistanceKm;

    private HomeOwnerPreferenceResponseDTO preferences;

    private List<String> missingFields;
}
