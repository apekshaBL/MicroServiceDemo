package com.example.studentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration.class})
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.example"}) // Broaden to pick up common-lib
@EntityScan(basePackages = {"com.example"})    // Find entities in common-lib
@EnableJpaRepositories(basePackages = {"com.example"}) // Find repos in common-lib
public class StudentServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(StudentServiceApplication.class, args);
    }
}