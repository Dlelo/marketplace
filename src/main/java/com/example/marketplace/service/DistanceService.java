package com.example.marketplace.service;

import com.example.marketplace.model.GeoLocation;
import org.springframework.stereotype.Service;

@Service
public class DistanceService {

    private static final int EARTH_RADIUS_KM = 6371;

    public double calculateKm(GeoLocation a, GeoLocation b) {
        if (a == null || b == null || !a.isValid() || !b.isValid()) {
            throw new IllegalArgumentException("Invalid geo location");
        }

        double latDistance = Math.toRadians(b.getLatitude() - a.getLatitude());
        double lonDistance = Math.toRadians(b.getLongitude() - a.getLongitude());

        double sinLat = Math.sin(latDistance / 2);
        double sinLon = Math.sin(lonDistance / 2);

        double h = sinLat * sinLat +
                Math.cos(Math.toRadians(a.getLatitude())) *
                        Math.cos(Math.toRadians(b.getLatitude())) *
                        sinLon * sinLon;

        double c = 2 * Math.atan2(Math.sqrt(h), Math.sqrt(1 - h));

        return EARTH_RADIUS_KM * c;
    }
}
