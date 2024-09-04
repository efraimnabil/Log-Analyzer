package com.service_health_monitor_portal.log_analyzer.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;

@Service
public class LogAnalyzerService {

    @Autowired
    private final InfluxDBService influxDBService;
    private final ObjectMapper objectMapper;
    private final String logFilePath;

    private static final Logger logger = LoggerFactory.getLogger(LogAnalyzerService.class);

    public LogAnalyzerService(InfluxDBService influxDBService,
                              @Value("${log.file.path}") String logFilePath,
                              ObjectMapper objectMapper) {
        this.influxDBService = influxDBService;
        this.logFilePath = logFilePath;
        this.objectMapper = objectMapper;
    }
    
    public void analyzeLogs() {
        System.out.println("Analyzing logs...");
        try (Stream<String> stream = Files.lines(Paths.get(logFilePath))) {
            System.out.println("Reading log file...");
            stream.forEach(this::processLogLine);
        } catch (IOException e) {
            System.out.println("Error reading log file");
            logger.error("Error reading log file", e);
        }
    }

    public void processLogLine(String logLine) {
        System.out.println(logLine);

        try {
            if (isValidJson(logLine)) {
                System.out.println("Extracting service log...");
                JsonNode logNode = objectMapper.readTree(logLine);
                if (logNode.has("service_log")) {
                    System.out.println("Extracted service log...");
                    JsonNode serviceLogNode = logNode.get("service_log");
                    logger.info("Extracted service log: {}", serviceLogNode.get("name"));
                    System.out.println("Extracted service log: " + serviceLogNode.get("name"));

                    Instant instant = getTimestamp(logNode);
                    String serviceName = serviceLogNode.get("name").asText();
                    String serviceId = serviceLogNode.get("id").asText();
                    String serviceStatus = serviceLogNode.get("status").asText();

                    Point point = Point.measurement("Service Health")
                            .addTag("name", serviceName)
                            .addTag("id", serviceId)
                            .addField("status", serviceStatus)
                            .time(instant, WritePrecision.MS);
                    System.out.println("Service Health point: " + point);
                    System.out.println("name: " + serviceName);
                    System.out.println("id: " + serviceId);
                    System.out.println("status: " + serviceStatus);

                    influxDBService.singlePointWrite(point);
                }
            } else {
                System.out.println("Invalid JSON format...");
                logger.warn("Invalid JSON format: {}", logLine);
            }
        } catch (IOException e) {
            System.out.println("Error parsing log line");
            logger.error("Error parsing log line", e);
        }
    }

    public Instant getTimestamp(JsonNode logNode) {
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

    public boolean isValidJson(String logLine) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            mapper.readTree(logLine);
            return true;
         } catch (IOException e) {
            return false;
         }
    }
}
