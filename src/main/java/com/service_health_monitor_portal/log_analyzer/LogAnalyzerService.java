package com.service_health_monitor_portal.log_analyzer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.stream.Stream;

@Service
public class LogAnalyzerService {

    @Autowired
    private InfluxDBService influxDBService;

    private static final Logger logger = LoggerFactory.getLogger(LogAnalyzerService.class);

    @Value("${log.file.path}")
    private String LOG_FILE_PATH;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Scheduled(fixedRate = 6000)
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

                    Instant instant = getTimestamp(logNode);
                    String serviceName = serviceLogNode.get("name").asText();
                    String serviceId = serviceLogNode.get("id").asText();
                    String serviceStatus = serviceLogNode.get("status").asText();

                    Point point = Point.measurement("Service Health")
                            .addTag("name", serviceName)
                            .addTag("id", serviceId)
                            .addField("status", serviceStatus)
                            .time(instant, WritePrecision.MS);

                    influxDBService.singlePointWrite(point);
                }
            } else {
                logger.warn("Invalid JSON format: {}", logLine);
            }
        } catch (IOException e) {
            logger.error("Error parsing log line", e);
        }
    }

    private Instant getTimestamp(JsonNode logNode) {
        JsonNode timestamp = logNode.get("@timestamp");
        Instant instant = Instant.now();
        if (timestamp == null) {
            logger.warn("No timestamp found in log line: {}", logNode);
        } else {
            instant = Instant.parse(timestamp.asText());
            logger.info("Timestamp: {}", instant);
        }
        return instant;
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
