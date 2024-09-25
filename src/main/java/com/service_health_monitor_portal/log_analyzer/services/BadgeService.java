package com.service_health_monitor_portal.log_analyzer.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.service_health_monitor_portal.log_analyzer.repository.BadgeRepository;
import com.service_health_monitor_portal.log_analyzer.dto.BadgeDTO;
import com.service_health_monitor_portal.log_analyzer.entity.BadgeEntity;

@Service
public class BadgeService {
    @Autowired
    private BadgeRepository badgeRepository;

    // Get All Badges
    public List<BadgeEntity> getAllBadges() {
        return badgeRepository.findAll();
    }

    // Get Badge By Id
    public BadgeEntity getBadgeById(Long id) {
        return badgeRepository.findById(id).orElse(null);
    }

    // Add Badge
    public BadgeEntity addBadge(BadgeDTO badge) {
        BadgeEntity newBadge = new BadgeEntity();
        newBadge.setName(badge.getName());
        return badgeRepository.save(newBadge);
    }

    // Delete Badge
    public void deleteBadge(Long id) {
        badgeRepository.deleteById(id);
    }
}
