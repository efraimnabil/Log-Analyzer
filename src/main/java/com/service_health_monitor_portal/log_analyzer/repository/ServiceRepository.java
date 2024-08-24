package com.service_health_monitor_portal.log_analyzer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.service_health_monitor_portal.log_analyzer.entity.ServiceEntity;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {
    ServiceEntity findById(long id);

    List<ServiceEntity> findByUserId(long userId);

    List<ServiceEntity> findAll();
}
