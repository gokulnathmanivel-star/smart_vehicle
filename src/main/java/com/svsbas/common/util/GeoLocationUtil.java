package com.svsbas.common.util;

public class GeoLocationUtil {

    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * Calculates the great-circle distance between two geographic coordinates
     * using the Haversine formula.
     *
     * @param lat1 Latitude of point 1 (degrees)
     * @param lon1 Longitude of point 1 (degrees)
     * @param lat2 Latitude of point 2 (degrees)
     * @param lon2 Longitude of point 2 (degrees)
     * @return Distance in kilometers
     */
    public static double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    /**
     * Estimates travel ETA in minutes assuming an average urban emergency response speed.
     *
     * @param distanceKm Distance in kilometers
     * @param avgSpeedKmh Average response speed in km/h (default ~30 km/h in urban traffic)
     * @return Estimated minutes
     */
    public static int estimateEtaMinutes(double distanceKm, double avgSpeedKmh) {
        if (avgSpeedKmh <= 0) avgSpeedKmh = 30.0;
        double hours = distanceKm / avgSpeedKmh;
        return (int) Math.ceil(hours * 60.0);
    }
}
