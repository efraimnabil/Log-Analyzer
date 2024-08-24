package com.service_health_monitor_portal.log_analyzer;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.service_health_monitor_portal.log_analyzer.services.LogAnalyzerService;

@SpringBootApplication
@EnableScheduling
public class LogAnalyzerApplication {

	public static void main(String[] args) {
		SpringApplication.run(LogAnalyzerApplication.class, args);
	}
	
	@Bean
    public CommandLineRunner commandLineRunner(LogAnalyzerService logAnalyzerService) {
        return args -> {
            logAnalyzerService.analyzeLogs();
        };
    }

}
