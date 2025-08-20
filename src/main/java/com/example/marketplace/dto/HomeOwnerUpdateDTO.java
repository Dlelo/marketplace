package com.example.marketplace.dto;

import lombok.Data;

@Data
public class HomeOwnerUpdateDTO {
    private String fullName;
    private String phoneNumber;
    private String email;
    private String nationalId;
    private String idDocument;
    private String homeLocation;
    private String houseType;   // e.g. Apartment, Bungalow
    private String numberOfRooms; // e.g. title deed scan
    private Integer numberOfDependents;// uploaded file path / reference
}
