package com.service_health_monitor_portal.log_analyzer.repository;

import com.service_health_monitor_portal.log_analyzer.entity.User;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    Optional<User> findById(Long id);
}