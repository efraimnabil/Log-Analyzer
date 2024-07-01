package com.service_health_monitor_portal.log_analyzer;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.write.Point;
import com.influxdb.exceptions.InfluxException;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public boolean writePointbyPOJO() {
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

    public List<AvailabilityData> calculateAvailabilityFromInfluxDB(String fluxQuery) {
        List<FluxTable> queryResult = queryData(fluxQuery);

        Map<Instant, Integer> successCounts = new HashMap<>();
        Map<Instant, Integer> throttlingErrorCounts = new HashMap<>();
        Map<Instant, Integer> dependencyErrorCounts = new HashMap<>();
        Map<Instant, Integer> faultErrorCounts = new HashMap<>();
        Map<Instant, Integer> invalidInputErrorCounts = new HashMap<>();
        Map<Instant, Integer> totalCounts = new HashMap<>();

        for (FluxTable table : queryResult) {
            List<FluxRecord> records = table.getRecords();
            for (FluxRecord record : records) {
                Instant timestamp = record.getTime();
                String field = record.getValueByKey("_field").toString();
                Integer value = Integer.valueOf(record.getValueByKey("_value").toString());

                totalCounts.put(timestamp, totalCounts.getOrDefault(timestamp, 0) + 1);

                if (field.equals("success") && value == 1) {
                    successCounts.put(timestamp, successCounts.getOrDefault(timestamp, 0) + 1);
                }

                if (field.equals("throttlingError") && value == 1) {
                    throttlingErrorCounts.put(timestamp, throttlingErrorCounts.getOrDefault(timestamp, 0) + 1);
                }

                if (field.equals("dependencyError") && value == 1) {
                    dependencyErrorCounts.put(timestamp, dependencyErrorCounts.getOrDefault(timestamp, 0) + 1);
                }

                if (field.equals("faultError") && value == 1) {
                    faultErrorCounts.put(timestamp, faultErrorCounts.getOrDefault(timestamp, 0) + 1);
                }

                if (field.equals("invalidInputError") && value == 1) {
                    invalidInputErrorCounts.put(timestamp, invalidInputErrorCounts.getOrDefault(timestamp, 0) + 1);
                }

            }
        }

        List<AvailabilityData> availabilityData = new ArrayList<>();
        for (Instant timestamp : totalCounts.keySet()) {
            int total = totalCounts.get(timestamp);
            int success = successCounts.getOrDefault(timestamp, 0);
            double availability = (double) success / total * 100;
            availabilityData.add(new AvailabilityData(timestamp, availability));
        }

        return availabilityData;
    }

    public List<FluxTable> queryData(String flux) {
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
        // for (FluxTable fluxTable : tables) {
        //     List<FluxRecord> records = fluxTable.getRecords();
        //     for (FluxRecord fluxRecord : records) {
        //         // System.out.println(fluxRecord.getValueByKey("sensor_id"));
        //         // here we will take actions
        //     }
        // }
        return tables;
    }
}
