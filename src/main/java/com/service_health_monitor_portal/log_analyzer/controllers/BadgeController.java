package com.service_health_monitor_portal.log_analyzer.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.service_health_monitor_portal.log_analyzer.dto.BadgeDTO;
import com.service_health_monitor_portal.log_analyzer.entity.BadgeEntity;
import com.service_health_monitor_portal.log_analyzer.services.BadgeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/badges")
@RequiredArgsConstructor
public class BadgeController {
    private final BadgeService badgeService;

    @GetMapping
    public ResponseEntity<List<BadgeEntity>> getAllBadges() {
        return ResponseEntity.ok(badgeService.getAllBadges());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BadgeEntity> getBadge(@PathVariable Long id) {
        BadgeEntity badge = badgeService.getBadgeById(id);
        if (badge == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(badge);
    }

    @PostMapping
    public ResponseEntity<BadgeEntity> addBadge(@RequestBody BadgeDTO badge) {
        return ResponseEntity.ok(badgeService.addBadge(badge));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBadge(@PathVariable Long id) {
        badgeService.deleteBadge(id);
        return ResponseEntity.noContent().build();
    }
}
