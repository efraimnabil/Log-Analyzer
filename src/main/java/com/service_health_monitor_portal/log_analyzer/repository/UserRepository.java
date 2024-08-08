package com.service_health_monitor_portal.log_analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.service_health_monitor_portal.log_analyzer.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
