package com.service_health_monitor_portal.log_analyzer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.ArgumentCaptor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.time.Instant;

public class LogAnalyzerServiceTest {

    @Mock
    private InfluxDBService influxDBService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private LogAnalyzerService logAnalyzerService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        logAnalyzerService = new LogAnalyzerService(influxDBService, "test_log_file_path", objectMapper);
    }

    @Test
    public void testProcessLogLine_ValidJson() throws IOException {
        String logLine = "{\"@timestamp\":\"2023-01-01T00:00:00Z\",\"service_log\":{\"name\":\"TestService\",\"id\":\"123\",\"status\":\"success\"}}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode logNode = mapper.readTree(logLine);
        assertNotNull(logNode);
        when(objectMapper.readTree(logLine)).thenReturn(logNode);

        logAnalyzerService.processLogLine(logLine);

        ArgumentCaptor<Point> pointCaptor = ArgumentCaptor.forClass(Point.class);
        verify(influxDBService, times(1)).singlePointWrite(pointCaptor.capture());

        Point actualPoint = pointCaptor.getValue();
        Point expectedPoint = Point.measurement("Service Health")
                .addTag("name", "TestService")
                .addTag("id", "123")
                .addField("status", "success")
                .time(Instant.parse("2023-01-01T00:00:00Z"), WritePrecision.MS);

        assertEquals(expectedPoint.toLineProtocol(), actualPoint.toLineProtocol());
    }

    @Test
    public void testGetTimestamp_WithTimestamp() throws IOException {

        String logLine = "{\"@timestamp\":\"2023-01-01T00:00:00Z\"}";
        JsonNode logNode = new ObjectMapper().readTree(logLine);

        Instant timestamp = logAnalyzerService.getTimestamp(logNode);

        assertEquals(Instant.parse("2023-01-01T00:00:00Z"), timestamp);
    }

    @Test
    public void testGetTimestamp_NoTimestamp() throws IOException {
        String logLine = "{}";
        JsonNode logNode = new ObjectMapper().readTree(logLine);

        Instant timestamp = logAnalyzerService.getTimestamp(logNode);

        assertNotNull(timestamp);
    }

    @Test
    public void testIsValidJson_ValidJson() {
        String logLine = "{\"key\":\"value\"}";

        boolean isValid = logAnalyzerService.isValidJson(logLine);

        assertTrue(isValid);
    }

    @Test
    public void testIsValidJson_InvalidJson() {
        String logLine = "{key:value}";

        boolean isValid = logAnalyzerService.isValidJson(logLine);

        assertFalse(isValid);
    }

}
