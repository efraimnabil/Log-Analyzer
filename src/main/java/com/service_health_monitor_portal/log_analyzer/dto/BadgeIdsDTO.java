package com.service_health_monitor_portal.log_analyzer.dto;

import java.util.List;

public class BadgeIdsDTO {
    private List<Long> badgeIds;

    // Getters and Setters
    public List<Long> getBadgeIds() {
        return badgeIds;
    }

    public void setBadgeIds(List<Long> badgeIds) {
        this.badgeIds = badgeIds;
    }
}