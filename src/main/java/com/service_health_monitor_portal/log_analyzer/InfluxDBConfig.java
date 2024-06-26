package com.service_health_monitor_portal.log_analyzer;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfluxDBConfig {

    @Value("${influxdb.url}")
    private String url;

    @Value("${influxdb.token}")
    private String token;

    @Value("${influxdb.bucket}")
    private String bucket;

    @Value("${influxdb.org}")
    private String org;

    @Bean
    public InfluxDBClient influxDBClient() {
        System.out.println("InfluxDB URL: " + url);
        System.out.println("InfluxDB Token: " + token);
        System.out.println("InfluxDB Bucket: " + bucket);
        System.out.println("InfluxDB Org: " + org);
        return InfluxDBClientFactory.create(url, token.toCharArray(), org, bucket);
    }
}
