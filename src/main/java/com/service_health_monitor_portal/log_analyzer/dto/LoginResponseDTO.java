package com.service_health_monitor_portal.log_analyzer.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginResponseDTO {
    private String token;
    private long expiresIn;
    private long id;
    private String name;
    private String email;
}
