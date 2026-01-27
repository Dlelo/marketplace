package com.example.marketplace.location;

import com.example.marketplace.model.GeoLocation;

public final class DistanceCalculator {

    private static final int EARTH_RADIUS_KM = 6371;

    private DistanceCalculator() {
        // utility class
    }

    /**
     * Calculates distance between two geo points in KM
     */
    public static double distanceKm(GeoLocation a, GeoLocation b) {
        if (a == null || b == null || !a.isValid() || !b.isValid()) {
            throw new IllegalArgumentException("Invalid geo location(s)");
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
