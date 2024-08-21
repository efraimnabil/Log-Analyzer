package com.service_health_monitor_portal.log_analyzer.repository;

import com.service_health_monitor_portal.log_analyzer.entity.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {
    ServiceEntity findById(long id);
}
