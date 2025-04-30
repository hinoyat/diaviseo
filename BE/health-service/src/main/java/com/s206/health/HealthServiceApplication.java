package com.s206.health;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.s206.health", "com.s206.common"})
public class HealthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(HealthServiceApplication.class, args);
	}

}
