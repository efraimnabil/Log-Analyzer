package com.service_health_monitor_portal.log_analyzer.controllers;

import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.service_health_monitor_portal.log_analyzer.dto.BadgeIdsDTO;
import com.service_health_monitor_portal.log_analyzer.entity.BadgeEntity;
import com.service_health_monitor_portal.log_analyzer.services.ServiceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class BadgeController {
    private final ServiceService serviceService;

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
}
