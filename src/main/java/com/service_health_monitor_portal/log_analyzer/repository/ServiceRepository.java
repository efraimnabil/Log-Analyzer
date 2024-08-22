package com.service_health_monitor_portal.log_analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.service_health_monitor_portal.log_analyzer.entity.ServiceEntity;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {
    ServiceEntity findById(long id);
}
