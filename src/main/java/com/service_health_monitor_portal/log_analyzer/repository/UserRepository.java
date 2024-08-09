package com.service_health_monitor_portal.log_analyzer.repository;

import com.service_health_monitor_portal.log_analyzer.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
@Repository
public interface UserRepository extends org.springframework.data.repository.Repository<User, Long> {
}