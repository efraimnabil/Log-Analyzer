package com.service_health_monitor_portal.log_analyzer.controllers;

import com.service_health_monitor_portal.log_analyzer.dto.ServiceDTO;
import com.service_health_monitor_portal.log_analyzer.entity.Service;
import com.service_health_monitor_portal.log_analyzer.services.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

    @Autowired
    private ServiceService serviceService;

    @PostMapping("/add")
    public ResponseEntity<Service> addService(
            @RequestParam Long userId,
            @RequestBody ServiceDTO serviceDTO) {
        try {
            Service service = serviceService.addService(userId, serviceDTO.getName());
            return new ResponseEntity<>(service, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Service> getService(@PathVariable Long id) {
        try {
            Service service = serviceService.getService(id);
            return new ResponseEntity<>(service, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<Iterable<Service>> getAllServices() {
        return new ResponseEntity<>(serviceService.getAllServices(), HttpStatus.OK);
    }
}
