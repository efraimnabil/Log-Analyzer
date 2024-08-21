package com.service_health_monitor_portal.log_analyzer.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.service_health_monitor_portal.log_analyzer.dto.ServiceDTO;
import com.service_health_monitor_portal.log_analyzer.entity.ServiceEntity;
import com.service_health_monitor_portal.log_analyzer.services.ServiceService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/services")
@Validated
public class ServiceController {

    @Autowired
    private ServiceService serviceService;

    @PostMapping
    public ResponseEntity<ServiceEntity> addService(
            @RequestParam @NotNull Long userId,
            @RequestBody @Valid ServiceDTO serviceDTO) {
        ServiceEntity service = serviceService.addService(userId, serviceDTO.getName());
        return new ResponseEntity<>(service, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceEntity> getService(@PathVariable @NotNull Long id) {
        ServiceEntity service = serviceService.getService(id);
        return new ResponseEntity<>(service, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Iterable<ServiceEntity>> getAllServices() {
        return new ResponseEntity<>(serviceService.getAllServices(), HttpStatus.OK);
    }
}
