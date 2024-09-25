package com.service_health_monitor_portal.log_analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.service_health_monitor_portal.log_analyzer.entity.BadgeEntity;

public interface BadgeRepository extends JpaRepository<BadgeEntity, Long> {
    
}
