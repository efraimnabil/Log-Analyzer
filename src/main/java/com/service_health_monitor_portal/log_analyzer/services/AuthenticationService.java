package com.service_health_monitor_portal.log_analyzer.services;

import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.service_health_monitor_portal.log_analyzer.dto.LoginResponseDTO;
import com.service_health_monitor_portal.log_analyzer.dto.LoginUserDTO;
import com.service_health_monitor_portal.log_analyzer.dto.RegisterUserDTO;
import com.service_health_monitor_portal.log_analyzer.entity.User;
import com.service_health_monitor_portal.log_analyzer.excetions.ResourceNotFoundException;
import com.service_health_monitor_portal.log_analyzer.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthenticationService {
    private final JwtService jwtService;

    private final UserRepository userRepository;
    
    private final PasswordEncoder passwordEncoder;
    
    private final AuthenticationManager authenticationManager;

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

    public User authenticateUser(LoginUserDTO input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );
        return userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    }

    public LoginResponseDTO authenticate(LoginUserDTO loginUserDto) {
        Optional<User> user = userRepository.findByEmail(loginUserDto.getEmail());
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User", "email or password", loginUserDto.getEmail());
        }
        if (!passwordEncoder.matches(loginUserDto.getPassword(), user.get().getPassword())) {
            throw new ResourceNotFoundException("User", "email or password", loginUserDto.getEmail());
        }

        User authenticatedUser = authenticateUser(loginUserDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponseDTO loginResponse = new LoginResponseDTO();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());
        return loginResponse;
    }
}
