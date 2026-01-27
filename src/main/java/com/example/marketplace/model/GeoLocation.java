package com.example.marketplace.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class GeoLocation {

    private Double latitude;
    private Double longitude;

    private String placeName;
    private String addressLine;

    public boolean isValid() {
        return latitude != null && longitude != null;
    }
}
