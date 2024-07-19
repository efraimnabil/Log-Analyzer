package com.service_health_monitor_portal.log_analyzer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

public class AvailabilityDataTest {

    private AvailabilityData availabilityData;

    @BeforeEach
    public void setUp() {
        availabilityData = new AvailabilityData(Instant.now(), 0.0);
    }

    @Test
    public void testGetTimestamp() {
        Instant timestamp = Instant.now();
        AvailabilityData availabilityData = new AvailabilityData(timestamp, 0.0);
        assertEquals(timestamp, availabilityData.getTimestamp());
    }

    @Test
    public void sumTest() {
        assertEquals(5, availabilityData.sum(2, 3));
    }
}
