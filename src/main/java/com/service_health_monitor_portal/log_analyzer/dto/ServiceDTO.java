package com.service_health_monitor_portal.log_analyzer.dto;

import java.sql.Timestamp;

public class ServiceDTO {
    private String name;
    private Timestamp createdAt;

    public String getName() {
        return name;
    }

    public ServiceDTO setName(String name) {
        this.name = name;
        return this;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public ServiceDTO setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
        return this;
    }
}