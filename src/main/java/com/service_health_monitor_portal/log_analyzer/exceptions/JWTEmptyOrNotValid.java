package com.service_health_monitor_portal.log_analyzer.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class JWTEmptyOrNotValid extends RuntimeException {
    
    public JWTEmptyOrNotValid() {
        super("JWT is empty or not valid");
    }

}
