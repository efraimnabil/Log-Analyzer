package com.service_health_monitor_portal.log_analyzer.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
 
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class UserAlreadyExistsException extends RuntimeException {
 
    public UserAlreadyExistsException(String message) {
        super(message);
    }
 
}