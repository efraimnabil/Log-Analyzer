package com.service_health_monitor_portal.log_analyzer.services;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.lang.reflect.Field;
import org.springframework.util.ReflectionUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.service_health_monitor_portal.log_analyzer.dto.ServiceDTO;
import com.service_health_monitor_portal.log_analyzer.dto.UpdateServiceDTO;
import com.service_health_monitor_portal.log_analyzer.entity.BadgeEntity;
import com.service_health_monitor_portal.log_analyzer.entity.ServiceEntity;
import com.service_health_monitor_portal.log_analyzer.entity.User;
import com.service_health_monitor_portal.log_analyzer.repository.BadgeRepository;
import com.service_health_monitor_portal.log_analyzer.repository.ServiceRepository;

@Service
public class ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private BadgeRepository badgeRepository;

    public ServiceEntity addService(User user, ServiceDTO serviceDTO) {
        ServiceEntity service = new ServiceEntity();
        service.setName(serviceDTO.getName());
        service.setDescription(serviceDTO.getDescription());
        List<BadgeEntity> fetchedBadges = badgeRepository.findAllById(serviceDTO.getBadgeIds());
        Set<BadgeEntity> badges = new HashSet<>(fetchedBadges);
        service.setBadges(badges);
        service.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        service.setUser(user);

        ServiceEntity savedService = serviceRepository.save(service);

        // TODO: Move this to a separate service {SimulatorService}
        String simulatorUrl = "http://localhost:7000/api/services";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("id", savedService.getId());
        requestBody.put("name", savedService.getName());
        requestBody.put("createdAt", savedService.getCreatedAt());
        requestBody.put("userId", savedService.getUser().getId());

        try {
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            restTemplate.postForEntity(simulatorUrl, request, String.class);
        } catch (ResourceAccessException e) {
            System.out.println(e);
        }

        return savedService;
    }

    public ServiceEntity getService(Long id) {
        Optional<ServiceEntity> service = serviceRepository.findById(id);
        return service.orElseThrow(() -> new RuntimeException("Service not found"));
    }

    public Iterable<ServiceEntity> getAllServices(Integer userId) {
        return serviceRepository.findByUserId(userId);
    }

    public Iterable<ServiceEntity> getAllServices() {
        return serviceRepository.findAll();
    }

    public void deleteService(ServiceEntity service) {
        serviceRepository.delete(service);
    }

    public void updateService(ServiceEntity service, UpdateServiceDTO updateService) {
        if (updateService.getName() != null) {
            service.setName(updateService.getName());
        }
        if (updateService.getDescription() != null) {
            service.setDescription(updateService.getDescription());
        }
        if (updateService.getBadgeIds() != null) {
            List<BadgeEntity> fetchedBadges = badgeRepository.findAllById(updateService.getBadgeIds());
            Set<BadgeEntity> badges = new HashSet<>(fetchedBadges);
            service.setBadges(badges);
        }
        serviceRepository.save(service);
    }

    public Set<BadgeEntity> getBadgesForService(Long serviceId) {
        ServiceEntity service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found"));
        return service.getBadges(); // Ensure this is returning a valid Set<BadgeEntity>
    }

    public void addBadgesToService(Long serviceId, List<Long> badgeIds) {
        ServiceEntity service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        // Check if badges exist
        Set<BadgeEntity> existingBadges = badgeRepository.findAllById(badgeIds).stream()
                .collect(Collectors.toSet());

        // Check for badges that do not exist
        List<Long> nonExistentBadgeIds = badgeIds.stream()
                .filter(id -> existingBadges.stream().noneMatch(badge -> badge.getId().equals(id)))
                .collect(Collectors.toList());

        if (!nonExistentBadgeIds.isEmpty()) {
            throw new RuntimeException("Badges not found for IDs: " + nonExistentBadgeIds);
        }

        service.getBadges().addAll(existingBadges);
        serviceRepository.save(service);
    }

    public void removeBadgesFromService(Long serviceId, List<Long> badgeIds) {
        ServiceEntity service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        Set<BadgeEntity> badgesToRemove = badgeRepository.findAllById(badgeIds).stream()
                .collect(Collectors.toSet());

        // Check for badges that do not exist in the service
        List<Long> nonExistentBadgeIds = badgeIds.stream()
                .filter(id -> service.getBadges().stream().noneMatch(badge -> badge.getId().equals(id)))
                .collect(Collectors.toList());

        if (!nonExistentBadgeIds.isEmpty()) {
            throw new RuntimeException("Badges not found in the service for IDs: " + nonExistentBadgeIds);
        }

        service.getBadges().removeAll(badgesToRemove);
        serviceRepository.save(service);
    }
}
