package com.service_health_monitor_portal.log_analyzer;


import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.exceptions.InfluxException;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;

public class InfluxDBConnectionClass {
    
    private String token;
    private String bucket;
    private String org;

    private String url;

    public InfluxDBClient buildConnection(String url, String token, String bucket, String org) {
        setToken(token);
        setBucket(bucket);
        setOrg(org);
        setUrl(url);
        return InfluxDBClientFactory.create(getUrl(), getToken().toCharArray(), getOrg(), getBucket());
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean singlePointWrite(InfluxDBClient influxDBClient, Point point) {
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

    public boolean writeMultiplePoints(InfluxDBClient influxDBClient, List<Point> listPoint) {
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

    public boolean writePointbyPOJO(InfluxDBClient influxDBClient) {
        boolean flag = false;
        try {
            WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();

            // Sensor sensor = new Sensor();
            // sensor.sensor_id = "TLM0101";
            // sensor.location = "Room 101";
            // sensor.model_number = "TLM89092A";
            // sensor.last_inspected = Instant.parse("2021-10-12T05:10:15.187484Z");

            // writeApi.writeMeasurement(WritePrecision.MS, Service);
            flag = true;
        } catch (

        InfluxException e) {
            System.out.println("Exception!!" + e.getMessage());
        }
        return flag;
    }

    

    public void queryData(InfluxDBClient influxDBClient, String flux) {
        // from(bucket: "myFirstBucket")
        // |> range(start: v.timeRangeStart, stop: v.timeRangeStop)
        // |> filter(fn: (r) => r["_measurement"] == "sensor")
        // |> filter(fn: (r) => r["_field"] == "model_number")
        // |> filter(fn: (r) => r["sensor_id"] == "TLM0100" or r["sensor_id"] ==
        // "TLM0101" or r["sensor_id"] == "TLM0103" or r["sensor_id"] == "TLM0200")
        // |> sort()
        // |> yield(name: "sort")

        QueryApi queryApi = influxDBClient.getQueryApi();

        List<FluxTable> tables = queryApi.query(flux);
        for (FluxTable fluxTable : tables) {
            List<FluxRecord> records = fluxTable.getRecords();
            for (FluxRecord fluxRecord : records) {
                // System.out.println(fluxRecord.getValueByKey("sensor_id"));
                // here we will take actions
            }
        }
    }
}
