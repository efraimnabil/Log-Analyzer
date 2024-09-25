package com.service_health_monitor_portal.log_analyzer.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BadgeDTO {

    @NotNull(message = "Badge's name is required")
    private String name;

}
