package com.service_health_monitor_portal.log_analyzer.controllers;

import java.security.Principal;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.service_health_monitor_portal.log_analyzer.dto.BadgeIdsDTO;
import com.service_health_monitor_portal.log_analyzer.dto.ServiceDTO;
import com.service_health_monitor_portal.log_analyzer.dto.UpdateServiceDTO;
import com.service_health_monitor_portal.log_analyzer.entity.BadgeEntity;
import com.service_health_monitor_portal.log_analyzer.entity.ServiceEntity;
import com.service_health_monitor_portal.log_analyzer.entity.User;
import com.service_health_monitor_portal.log_analyzer.services.BadgeService;
import com.service_health_monitor_portal.log_analyzer.services.ServiceService;
import com.service_health_monitor_portal.log_analyzer.services.UserService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/services")
@Validated
public class ServiceController {

    @Autowired
    private ServiceService serviceService;

    @Autowired
    private UserService userService;

    @Autowired
    private BadgeService badgeService;

    @PostMapping
    public ResponseEntity<ServiceEntity> addService(
            @RequestBody @Valid ServiceDTO serviceDTO, Principal principal) {
        try {
            System.out.println(serviceDTO);
            User user = userService.getUser(principal.getName());
            ServiceEntity service = serviceService.addService(user, serviceDTO);
            userService.addService(user, service);
            return new ResponseEntity<>(service, HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceEntity> getService(@PathVariable @NotNull Long id, Principal principal) {
        try {
            ServiceEntity service = serviceService.getService(id);
            if (!service.getUser().getEmail().equals(principal.getName()))
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            return new ResponseEntity<>(service, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> updateService(@PathVariable @NotNull Long id,
            @RequestBody @Valid UpdateServiceDTO updateService, Principal principal) {
        try {
            ServiceEntity service = serviceService.getService(id);
            if (!service.getUser().getEmail().equals(principal.getName()))
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            serviceService.updateService(service, updateService);
            return new ResponseEntity<>("Service Updated Successfully", HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Service Not Found", HttpStatus.NOT_FOUND);
        }
    }

    // Get all badges for a specific service
    @GetMapping("/{serviceId}/badges")
    public ResponseEntity<?> getBadges(@PathVariable Long serviceId) {
        try {
            Set<BadgeEntity> badges = serviceService.getBadgesForService(serviceId);
            return ResponseEntity.ok(badges);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error fetching badges for service ID " + serviceId + ": " + e.getMessage());
        }
    }

    // Add badges to a service
    @PostMapping("/{serviceId}/badges")
    public ResponseEntity<String> addBadges(@PathVariable Long serviceId, @RequestBody BadgeIdsDTO badgeIdsDTO) {
        List<Long> badgeIds = badgeIdsDTO.getBadgeIds();
        try {
            serviceService.addBadgesToService(serviceId, badgeIds);
            return ResponseEntity.ok("Badges added successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error adding badges to service ID " + serviceId + ": " + e.getMessage());
        }
    }

    // Remove badges from a service
    @DeleteMapping("/{serviceId}/badges")
    public ResponseEntity<String> removeBadges(@PathVariable Long serviceId, @RequestBody BadgeIdsDTO badgeIdsDTO) {
        List<Long> badgeIds = badgeIdsDTO.getBadgeIds();
        try {
            serviceService.removeBadgesFromService(serviceId, badgeIds);
            return ResponseEntity.ok("Badges removed successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error removing badges from service ID " + serviceId + ": " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<Iterable<ServiceEntity>> getAllServices(Principal principal) {
        Iterable<ServiceEntity> services = serviceService
                .getAllServices(userService.getUser(principal.getName()).getId());
        return new ResponseEntity<>(services, HttpStatus.OK);
    }

    @GetMapping("/all")
    @CrossOrigin(origins = "http://localhost:7000")
    public ResponseEntity<Iterable<ServiceEntity>> getAllServices() {
        Iterable<ServiceEntity> services = serviceService.getAllServices();
        return new ResponseEntity<>(services, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteServiceById(@PathVariable @NotNull Long id, Principal principal) {
        try {
            ServiceEntity service = serviceService.getService(id);
            if (!service.getUser().getEmail().equals(principal.getName()))
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            serviceService.deleteService(service);
            return new ResponseEntity<>("Service Deleted Successfully",
                    HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>("Service Not Found", HttpStatus.NOT_FOUND);
        }
    }
}
