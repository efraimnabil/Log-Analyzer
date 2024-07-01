package com.service_health_monitor_portal.log_analyzer;

import com.influxdb.client.write.Point;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @Autowired
    private InfluxDBService influxDBService;

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

            boolean result = influxDBService.singlePointWrite(point);
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

    @GetMapping("/services/{serviceId}")
    public ResponseEntity<List<AvailabilityData>> getServiceDataById(@PathVariable("serviceId") Integer serviceId) {
        try {
            System.out.println("Service ID: " + serviceId);
            String fluxQuery = "from(bucket: \"Services\") |> range(start: -7d) |> filter(fn: (r) => r[\"id\"] == \"" + serviceId + "\")";
            System.err.println(fluxQuery);
            List<AvailabilityData> queryResult = influxDBService.calculateAvailabilityFromInfluxDB(fluxQuery);

            if (queryResult.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(queryResult);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}
