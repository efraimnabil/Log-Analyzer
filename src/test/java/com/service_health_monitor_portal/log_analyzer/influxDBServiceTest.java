package com.service_health_monitor_portal.log_analyzer;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.write.Point;

import java.util.Collections;
import java.util.List; // Add this import statement

public class influxDBServiceTest {
    @Mock
    private InfluxDBClient influxDBClient;

    @Mock
    private WriteApiBlocking writeApiBlocking;

    @InjectMocks
    private InfluxDBService influxDBService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(influxDBClient.getWriteApiBlocking()).thenReturn(writeApiBlocking);
    }

    @Test
    public void testSinglePointWrite_Success() {

        assertTrue(influxDBService.singlePointWrite(Point.measurement("test_measurement")));
        verify(writeApiBlocking, times(1)).writePoint(any(Point.class));

    }
    
    @Test
    public void testWriteMultiplePoints_Success() {
        List<Point> points = Collections.singletonList(Point.measurement("test_measurement"));
        assertTrue(influxDBService.writeMultiplePoints(points));
        verify(writeApiBlocking, times(1)).writePoints(points);
    }

    @Test
    public void testWritePointbyPOJO_Success() {
        assertTrue(influxDBService.writePointbyPOJO());
    }


    @Test
    public void testCalculateAvailabilityFromInfluxDB_Success() {

    }
    



}
