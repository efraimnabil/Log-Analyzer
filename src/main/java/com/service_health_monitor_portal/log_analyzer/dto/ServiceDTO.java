package com.service_health_monitor_portal.log_analyzer.dto;

import java.sql.Timestamp;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ServiceDTO {

    private Long id;

    @NotNull(message = "Service name is required")
    @Pattern(regexp = "^[a-zA-Z0-9_]*$", message = "Service name must contain only alphanumeric characters and underscores")
    private String name;
    
    private Timestamp createdAt;
}