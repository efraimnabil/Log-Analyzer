package com.service_health_monitor_portal.log_analyzer.services;

import java.sql.Timestamp;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import com.service_health_monitor_portal.log_analyzer.entity.Service;
import com.service_health_monitor_portal.log_analyzer.entity.User;
import com.service_health_monitor_portal.log_analyzer.repository.UserRepository;
import com.service_health_monitor_portal.log_analyzer.repository.ServiceRepository;

public class ServiceService {
    
    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private UserRepository userRepository;

    public Service addService(Long userId, String name) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            Service service = new Service();
            service.setName(name);
            service.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            service.setUser(user);
            return serviceRepository.save(service);
        }
        throw new RuntimeException("User not found");
    }

    public Service getService(Long id) {
        Optional<Service> serviceOpt = serviceRepository.findById(id);
        if (serviceOpt.isPresent()) {
            return serviceOpt.get();
        }
        throw new RuntimeException("Service not found");
    }

    public Iterable<Service> getAllServices() {
        return serviceRepository.findAll();
    }
}
