package com.service_health_monitor_portal.log_analyzer.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.service_health_monitor_portal.log_analyzer.entity.User;
import com.service_health_monitor_portal.log_analyzer.dto.LoginUserDTO;
import com.service_health_monitor_portal.log_analyzer.dto.RegisterUserDTO;
import com.service_health_monitor_portal.log_analyzer.repository.UserRepository;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    
    private final PasswordEncoder passwordEncoder;
    
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
        UserRepository userRepository,
        AuthenticationManager authenticationManager,
        PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User signup(RegisterUserDTO input) {
        User user = new User();
        user.setName(input.getName());
        user.setEmail(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        java.sql.Timestamp currTime = new java.sql.Timestamp(System.currentTimeMillis());
        user.setCreatedAt(currTime);
        userRepository.save(user);
        return user;
    }

    public User authenticate(LoginUserDTO input) {
        System.out.println("before authenticate");
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );
        System.out.println("after authenticate");
        System.out.println(userRepository.findByEmail(input.getEmail()));
        return userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    }
}
