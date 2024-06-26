package com.service_health_monitor_portal.log_analyzer;

import com.influxdb.client.write.Point;
import com.service_health_monitor_portal.log_analyzer.InfluxDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.influxdb.client.domain.WritePrecision;


import java.time.Instant;

@RestController
@RequestMapping("/influxdb")
public class InfluxDBController {

    private final InfluxDBService influxDBService;

    @Autowired
    public InfluxDBController(InfluxDBService influxDBService) {
        this.influxDBService = influxDBService;
    }

    @PostMapping("/write")
    public String writeData() {
        Point point = Point.measurement("example_measurement")
                .addTag("example_tag", "tag_value")
                .addField("example_field", 500)
                .time(Instant.now(), WritePrecision.MS);

        boolean result = influxDBService.writeSinglePoint(point);
        return result ? "Write Successful" : "Write Failed";
    }

    @GetMapping("/query")
    public void queryData() {
        String fluxQuery = "from(bucket: \"your_bucket_here\") |> range(start: -1h)";
        influxDBService.queryData(fluxQuery);
    }
}
