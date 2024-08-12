package com.service_health_monitor_portal.log_analyzer.repository;

import com.service_health_monitor_portal.log_analyzer.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    Service findById(long id);
}
