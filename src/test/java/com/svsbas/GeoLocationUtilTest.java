package com.svsbas;

import com.svsbas.common.util.GeoLocationUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeoLocationUtilTest {

    @Test
    @DisplayName("Should return 0 distance for identical coordinates")
    void testIdenticalCoordinates() {
        double dist = GeoLocationUtil.calculateDistanceKm(12.9716, 77.5946, 12.9716, 77.5946);
        assertEquals(0.0, dist, 0.001);
    }

    @Test
    @DisplayName("Should accurately compute distance between two urban points")
    void testUrbanDistance() {
        // Distance between Bangalore MG Road (12.9756, 77.6066) and Indiranagar (12.9784, 77.6408) is ~3.7 km
        double dist = GeoLocationUtil.calculateDistanceKm(12.9756, 77.6066, 12.9784, 77.6408);
        assertTrue(dist > 3.0 && dist < 4.5, "Distance should be approximately 3.7 km");
    }

    @Test
    @DisplayName("Should estimate ETA reasonably given urban speeds")
    void testEstimateEtaMinutes() {
        // 15 km at 30 km/h should take 30 minutes
        int eta = GeoLocationUtil.estimateEtaMinutes(15.0, 30.0);
        assertEquals(30, eta);
    }
}
