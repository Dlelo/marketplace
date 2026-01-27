package com.example.marketplace.dto;

import lombok.Data;

@Data
public class GeoLocationUpdateDTO {
    private Double latitude;
    private Double longitude;
    private String placeName;
    private String addressLine;
}
