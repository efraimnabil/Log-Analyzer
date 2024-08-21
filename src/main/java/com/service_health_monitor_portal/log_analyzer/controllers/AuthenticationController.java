package com.service_health_monitor_portal.log_analyzer.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.service_health_monitor_portal.log_analyzer.services.JwtService;
import com.service_health_monitor_portal.log_analyzer.dto.LoginResponseDTO;
import com.service_health_monitor_portal.log_analyzer.dto.LoginUserDTO;
import com.service_health_monitor_portal.log_analyzer.dto.RegisterUserDTO;
import com.service_health_monitor_portal.log_analyzer.entity.User;
import com.service_health_monitor_portal.log_analyzer.services.AuthenticationService;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;
    
    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterUserDTO registerUserDto) {
        User registeredUser = authenticationService.signup(registerUserDto);

        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> authenticate(@RequestBody LoginUserDTO loginUserDto) {
        System.out.println("Login request received");
        System.out.println(loginUserDto.getEmail());
        System.out.println(loginUserDto.getPassword());
        User authenticatedUser = authenticationService.authenticate(loginUserDto);
        System.out.println("User authenticated");
        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponseDTO loginResponse = new LoginResponseDTO();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());

        return ResponseEntity.ok(loginResponse);
    }
}
