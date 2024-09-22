package com.service_health_monitor_portal.log_analyzer.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.service_health_monitor_portal.log_analyzer.entity.ServiceEntity;
import com.service_health_monitor_portal.log_analyzer.entity.User;
import com.service_health_monitor_portal.log_analyzer.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public boolean isUserExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean isUserExists(Long id) {
        return userRepository.findById(id).isPresent();
    }

    public User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void addService(User user, ServiceEntity service) {
        user.getServices().add(service);
        userRepository.save(user);
    }
}
