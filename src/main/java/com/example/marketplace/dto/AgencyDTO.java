package com.example.marketplace.dto;

import lombok.Data;

/** Request body for creating or updating an Agency entity. */
@Data
public class AgencyDTO {
    private String name;
    private String phoneNumber;
    private String email;
    private String locationOfOperation;
    private String homeLocation;
    private String houseNumber;
}
