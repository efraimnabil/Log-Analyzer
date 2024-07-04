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
import org.springframework.web.bind.annotation.RequestParam;
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
            String fluxQuery = "from(bucket: \"Services\") |> range(start: , end: 0) |> group(columns: [\"id\"])";
            
            List<FluxTable> queryResult = influxDBService.queryData(fluxQuery);
            List<ServiceMetadata> services = new ArrayList<>();
            for (FluxTable table : queryResult) {
                String serviceName = table.getRecords().get(0).getValueByKey("_measurement").toString();
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
        String id
    ) {}

    @GetMapping("/services/{serviceId}")
    public ResponseEntity<Map<String, List<StateRateTime>>> getServiceDataById(
            @PathVariable("serviceId") String serviceId,
            @RequestParam(defaultValue = "1970-01-01T00:00:00Z") Instant startDate,
            @RequestParam(defaultValue = "#{T(java.time.Instant).now()}") Instant endDate) {
        try {
            String fluxQuery = "from(bucket: \"Services\") |> range(start: " + startDate + ", stop: " + endDate + ") |> filter(fn: (r) => r[\"id\"] == \"" + serviceId + "\")";

            List<FluxTable> queryResult = influxDBService.queryData(fluxQuery);

            if (queryResult.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            // group the states by the field name
            Map<String, List<Integer>> stateLists = new HashMap<>();
            for (FluxTable fluxTable : queryResult) {
                List<FluxRecord> records = fluxTable.getRecords();
                for (FluxRecord record : records) {
                    stateLists.putIfAbsent(record.getValueByKey("_field").toString(), new ArrayList<>());
                    stateLists.get(record.getValueByKey("_field").toString()).add(Integer.parseInt(record.getValueByKey("_value").toString()));
                }
            }

            // average the values for every 4 intervals
            // TODO: get the interval count from the user
            final int intervalCount = 4;
            Map<String, List<StateRateTime>> averagedStateLists = new HashMap<>();
            for (String key : stateLists.keySet()) {
                List<Integer> stateList = stateLists.get(key);
                List<StateRateTime> averagedStateList = new ArrayList<>();
                for (int i = 0; i < stateList.size(); i += intervalCount) {
                    int sum = 0;
                    for (int j = i; j < Math.min(i + intervalCount, stateList.size()); j++) {
                        sum += stateList.get(j);
                    }
                    double rate = 100.0 * sum / intervalCount;
                    Instant time = queryResult.get(0).getRecords().get(Math.min(i + intervalCount, stateList.size()) - 1).getTime();
                    averagedStateList.add(new StateRateTime(rate, time));
                }
                averagedStateLists.put(key, averagedStateList);
            }
            return ResponseEntity.ok(averagedStateLists);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    record StateRateTime(
        Double rate,
        Instant time
    ) {
    }
}
