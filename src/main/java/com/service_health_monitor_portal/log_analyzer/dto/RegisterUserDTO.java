package com.service_health_monitor_portal.log_analyzer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisterUserDTO {

    @NotNull(message = "Name is required")
    @Pattern(regexp = "^[a-zA-Z ]*$", message = "Name must contain only alphabets and spaces")
    private String name;
    
    @NotNull(message = "Email is required")
    @Email(message = "Invalid email")
    private String email;
    
    @NotNull(message = "Password is required")
    @Pattern(regexp = ".{8,}", message = "Password must be at least 8 characters long")
    private String password;
}