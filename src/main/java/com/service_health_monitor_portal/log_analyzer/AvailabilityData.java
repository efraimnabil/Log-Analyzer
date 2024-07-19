package com.service_health_monitor_portal.log_analyzer;

import java.time.Instant;

public class AvailabilityData {
    private Instant timestamp;
    private double availability;

    public AvailabilityData(Instant timestamp, double availability) {
        this.timestamp = timestamp;
        this.availability = availability;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public double getAvailability() {
        return availability;
    }

    public void setAvailability(double availability) {
        this.availability = availability;
    }
    public int sum(int a, int b) {
        return a + b;
    }
}
