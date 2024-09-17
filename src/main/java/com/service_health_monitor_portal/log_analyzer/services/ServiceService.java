package com.service_health_monitor_portal.log_analyzer.services;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.service_health_monitor_portal.log_analyzer.entity.ServiceEntity;
import com.service_health_monitor_portal.log_analyzer.entity.User;
import com.service_health_monitor_portal.log_analyzer.repository.ServiceRepository;
import com.service_health_monitor_portal.log_analyzer.repository.UserRepository;

@Service
public class ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    public ServiceEntity addService(Long userId, String name) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            ServiceEntity service = new ServiceEntity();
            service.setName(name);
            service.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            service.setUser(user);

            ServiceEntity savedService = serviceRepository.save(service);

            String simulatorUrl = "http://localhost:7000/api/services";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("id", savedService.getId());
            requestBody.put("name", savedService.getName());
            requestBody.put("createdAt", savedService.getCreatedAt());
            requestBody.put("userId", savedService.getUser().getId());

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            restTemplate.postForEntity(simulatorUrl, request, String.class);

            return savedService;
        }
        throw new RuntimeException("User not found");
    }

    public ServiceEntity getService(Long id) {
        Optional<ServiceEntity> serviceOpt = serviceRepository.findById(id);
        if (serviceOpt.isPresent()) {
            return serviceOpt.get();
        }
        throw new RuntimeException("Service not found");
    }

    public Iterable<ServiceEntity> getAllServices(Long userId) {
        return serviceRepository.findByUserId(userId);
    }

    public Iterable<ServiceEntity> getAllServices() {
        return serviceRepository.findAll();
    }
}
