package com.service_health_monitor_portal.log_analyzer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Service
public class LogAnalyzerService {

    private static final Logger logger = LoggerFactory.getLogger(LogAnalyzerService.class);
    private static final String LOG_FILE_PATH = "/home/fero/Desktop/service-health-monitor-portal/Simulator-Service/services_logs/simulator.log";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void analyzeLogs() {
        try (Stream<String> stream = Files.lines(Paths.get(LOG_FILE_PATH))) {
            stream.forEach(this::processLogLine);
        } catch (IOException e) {
            logger.error("Error reading log file", e);
        }
    }

    private void processLogLine(String logLine) {
        try {
            if (isValidJson(logLine)) {
                JsonNode logNode = objectMapper.readTree(logLine);
                if (logNode.has("service_log")) {
                    JsonNode serviceLogNode = logNode.get("service_log");
                    logger.info("Extracted service log: {}", serviceLogNode.get("name"));
                    // Further process the service_log object if needed
                }
            } else {
                logger.warn("Invalid JSON format: {}", logLine);
            }
        } catch (IOException e) {
            logger.error("Error parsing log line", e);
        }
    }

    private boolean isValidJson(String logLine) {
        try {
            objectMapper.readTree(logLine);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}