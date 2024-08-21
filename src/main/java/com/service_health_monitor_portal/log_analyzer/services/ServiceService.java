package com.service_health_monitor_portal.log_analyzer.services;

import java.sql.Timestamp;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import com.service_health_monitor_portal.log_analyzer.entity.ServiceEntity;
import com.service_health_monitor_portal.log_analyzer.entity.User;
import com.service_health_monitor_portal.log_analyzer.repository.UserRepository;
import com.service_health_monitor_portal.log_analyzer.repository.ServiceRepository;
import org.springframework.stereotype.Service;

@Service
public class ServiceService {
    
    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private UserRepository userRepository;

    public ServiceEntity addService(Long userId, String name) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            ServiceEntity service = new ServiceEntity();
            service.setName(name);
            service.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            service.setUser(user);
            return serviceRepository.save(service);
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

    public Iterable<ServiceEntity> getAllServices() {
        return serviceRepository.findAll();
    }
}
