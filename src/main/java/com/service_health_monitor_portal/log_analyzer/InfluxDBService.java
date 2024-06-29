package com.service_health_monitor_portal.log_analyzer;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxTable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.service_health_monitor_portal.log_analyzer.InfluxDBConnectionClass;

import java.util.List;

@Service
public class InfluxDBService {

    private final InfluxDBClient influxDBClient;
    private final InfluxDBConnectionClass influxDBConnectionClass;

    @Autowired
    public InfluxDBService(InfluxDBClient influxDBClient) {
        this.influxDBClient = influxDBClient;
        this.influxDBConnectionClass = new InfluxDBConnectionClass();
    }

    public boolean writeSinglePoint(Point point) {
        return influxDBConnectionClass.singlePointWrite(influxDBClient, point);
    }

    public boolean writeMultiplePoints(List<Point> points) {
        return influxDBConnectionClass.writeMultiplePoints(influxDBClient, points);
    }

    public List<FluxTable> queryData(String fluxQuery) {
        return influxDBConnectionClass.queryData(influxDBClient, fluxQuery);
    }
}
