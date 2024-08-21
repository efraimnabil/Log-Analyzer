package com.service_health_monitor_portal.log_analyzer.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.service_health_monitor_portal.log_analyzer.dto.LoginResponseDTO;
import com.service_health_monitor_portal.log_analyzer.dto.LoginUserDTO;
import com.service_health_monitor_portal.log_analyzer.dto.RegisterUserDTO;
import com.service_health_monitor_portal.log_analyzer.entity.User;
import com.service_health_monitor_portal.log_analyzer.services.AuthenticationService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RequestMapping("/auth")
@RestController
@Validated
@AllArgsConstructor
public class AuthenticationController {    
    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody @Valid RegisterUserDTO registerUserDto) {
        User registeredUser = authenticationService.signup(registerUserDto);

        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> authenticate(@RequestBody @Valid LoginUserDTO loginUserDto) {
        LoginResponseDTO loginResponse = authenticationService.authenticate(loginUserDto);

        return ResponseEntity.ok(loginResponse);
    }
}
