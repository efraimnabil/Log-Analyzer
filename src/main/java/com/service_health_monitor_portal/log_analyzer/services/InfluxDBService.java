package com.service_health_monitor_portal.log_analyzer.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.write.Point;
import com.influxdb.exceptions.InfluxException;
import com.influxdb.query.FluxTable;

@Service
public class InfluxDBService {

    @Autowired
    private InfluxDBClient influxDBClient;

    public boolean singlePointWrite(Point point) {
        boolean flag = false;
        try {
            WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();

            writeApi.writePoint(point);
            flag = true;
        } catch (InfluxException e) {
            System.out.println("Exception!!" + e.getMessage());
        }
        return flag;
    }

    public boolean writeMultiplePoints(List<Point> listPoint) {
        boolean flag = false;
        try {
            WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();

            writeApi.writePoints(listPoint);
            // signifies write is done successfully
            flag = true;
        } catch (InfluxException e) {
            System.out.println("Exception!!" + e.getMessage());
        }
        return flag;
    }

    // public boolean writePointbyPOJO() {
    //     boolean flag = false;
    //     try {
    //         WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();

    //         // Sensor sensor = new Sensor();
    //         // sensor.sensor_id = "TLM0101";
    //         // sensor.location = "Room 101";
    //         // sensor.model_number = "TLM89092A";
    //         // sensor.last_inspected = Instant.parse("2021-10-12T05:10:15.187484Z");

    //         // writeApi.writeMeasurement(WritePrecision.MS, Service);
    //         flag = true;
    //     } catch (

    //     InfluxException e) {
    //         System.out.println("Exception!!" + e.getMessage());
    //     }
    //     return flag;
    // }

    public List<FluxTable> queryData(String flux) {
        QueryApi queryApi = influxDBClient.getQueryApi();
        List<FluxTable> tables = queryApi.query(flux);
        return tables;
    }
}
