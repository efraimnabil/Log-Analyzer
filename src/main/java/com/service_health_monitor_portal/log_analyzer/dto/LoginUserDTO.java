package com.service_health_monitor_portal.log_analyzer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginUserDTO {

    @NotNull(message = "Email is required")
    @Email(message = "Invalid email")
    private String email;

    @NotNull(message = "Password is required")
    private String password;
}
