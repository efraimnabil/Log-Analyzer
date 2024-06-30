package com.service_health_monitor_portal.log_analyzer;

import com.influxdb.client.write.Point;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.influxdb.client.domain.WritePrecision;

import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/influxdb")
@CrossOrigin(origins = "http://localhost:5173")
public class InfluxDBController {

    private final InfluxDBService influxDBService;

    @Autowired
    public InfluxDBController(InfluxDBService influxDBService) {
        this.influxDBService = influxDBService;
    }

    @GetMapping("/availability")
    public ResponseEntity<List<AvailabilityData>> calculateAvailability() {
        try {
            List<AvailabilityData> availabilityData = calculateAvailabilityFromInfluxDB();
            return ResponseEntity.ok(availabilityData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ArrayList<>());
        }
    }

    private List<AvailabilityData> calculateAvailabilityFromInfluxDB() {
        String fluxQuery = "from(bucket: \"Services\") |> range(start: -7d) |> filter(fn: (r) => r._measurement == \"efraimService\")";

        List<FluxTable> queryResult = influxDBService.queryData(fluxQuery);

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

    @PostMapping("/write")
    public ResponseEntity<String> writeData() {
        try {
            Point point = Point.measurement("efraimService")
                    .addField("success", 1) // Example fields, adjust as per your schema
                    .addField("throttlingError", 0)
                    .addField("dependencyError", 0)
                    .addField("faultError", 1)
                    .addField("invalidInputError", 0)
                    .time(Instant.now(), WritePrecision.MS);

            boolean result = influxDBService.writeSinglePoint(point);
            return result ? ResponseEntity.ok("Write Successful") : ResponseEntity.badRequest().body("Write Failed");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to write data: " + e.getMessage());
        }
    }

    @GetMapping("/query")
    public ResponseEntity<List<FluxTable>> queryData() {
        try {
            String fluxQuery = "from(bucket: \"Services\") |> range(start: -7d) |> filter(fn: (r) => r._measurement == \"efraimService\") |> filter(fn: (r) => r[\"_field\"] == \"success\")";
            
            List<FluxTable> queryResult = influxDBService.queryData(fluxQuery);
            return ResponseEntity.ok(queryResult);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    @GetMapping("/services")
    public ResponseEntity<List<ServiceMetadata>> getAllServices() {
        try {
            // TODO: get all services from MySQL database
            String fluxQuery = "from(bucket: \"Services\") |> range(start: -7d) |> group(columns: [\"_measurement\"])";
            
            List<FluxTable> queryResult = influxDBService.queryData(fluxQuery);
            List<ServiceMetadata> services = new ArrayList<>();
            for (FluxTable table : queryResult) {
                String serviceName = table.getRecords().get(0).getValueByKey("_measurement").toString();
                int id = Integer.parseInt(table.getRecords().get(0).getValueByKey("id").toString());
                services.add(new ServiceMetadata(serviceName, id));
            }
            return ResponseEntity.ok(services);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    record ServiceMetadata(
        String name,
        int id
    ) {}
}
