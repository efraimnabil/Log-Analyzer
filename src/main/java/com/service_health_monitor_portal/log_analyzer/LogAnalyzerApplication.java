package com.service_health_monitor_portal.log_analyzer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.service_health_monitor_portal.log_analyzer.services.LogAnalyzerService;

@SpringBootApplication
@EnableScheduling
public class LogAnalyzerApplication {

    @Autowired
    private LogAnalyzerService logAnalyzerService;

    public static void main(String[] args) {
        SpringApplication.run(LogAnalyzerApplication.class, args);
    }

    @Scheduled(fixedRate = 600000)
    public void scheduledLogAnalysis() {
        logAnalyzerService.analyzeLogs();
    }
}
