package com.service_health_monitor_portal.log_analyzer.controllers;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import com.service_health_monitor_portal.log_analyzer.services.InfluxDBService;

@RestController
@RequestMapping("/api/influxdb")
public class InfluxDBController {

    @Autowired
    private InfluxDBService influxDBService;

    @GetMapping("/services")
    public ResponseEntity<List<ServiceMetadata>> getAllServices() {
        try {
            // TODO: get all services from MySQL database
            String fluxQuery = "from(bucket: \"Services\") |> range(start: 1970-01-01T00:00:00Z) |> group(columns: [\"id\"])";

            List<FluxTable> queryResult = influxDBService.queryData(fluxQuery);
            List<ServiceMetadata> services = new ArrayList<>();
            for (FluxTable table : queryResult) {
                String serviceName = table.getRecords().get(0).getValueByKey("name").toString();
                String id = table.getRecords().get(0).getValueByKey("id").toString();
                services.add(new ServiceMetadata(serviceName, id));
            }
            return ResponseEntity.ok(services);
        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
            for (StackTraceElement element : e.getStackTrace()) {
                System.out.println(element);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    record ServiceMetadata(
            String name,
            String id) {
    }

    @GetMapping("/services/{serviceId}")
    public ResponseEntity<List<TypeFlagTime>> getServiceDataById(
            @PathVariable("serviceId") String serviceId,
            @RequestParam(defaultValue = "1970-01-01T00:00:00Z") Instant startDate,
            @RequestParam(defaultValue = "#{T(java.time.Instant).now()}") Instant endDate,
            @RequestParam(name = "type", required = false) List<String> types) {
        try {
            String fluxQuery = "from(bucket: \"Services\") |> range(start: " + startDate + ", stop: " + endDate
                    + ") |> filter(fn: (r) => r[\"id\"] == \"" + serviceId + "\")";

            List<FluxTable> queryResult = influxDBService.queryData(fluxQuery);

            if (queryResult.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            // group the states by the field name
            List<TypeFlagTime> stateLists = new ArrayList<>();
            for (FluxTable fluxTable : queryResult) {
                List<FluxRecord> records = fluxTable.getRecords();
                for (FluxRecord record : records) {
                    String status = record.getValueByKey("_value").toString();
                    if (types == null || types.contains(status)) {
                        TypeFlagTime temp = new TypeFlagTime(status, record.getTime());
                        stateLists.add(temp);
                    }
                }
            }

            return ResponseEntity.ok(stateLists);
        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    record TypeFlagTime(
            String status,
            Instant time) {
    }
}
