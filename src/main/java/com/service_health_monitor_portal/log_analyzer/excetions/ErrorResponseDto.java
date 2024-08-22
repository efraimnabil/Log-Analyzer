package com.service_health_monitor_portal.log_analyzer.excetions;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponseDto {
 
    private String apiPath;
 
    private HttpStatus errorCode;
 
    private String errorMessage;
 
    private LocalDateTime errorTime;
}
 