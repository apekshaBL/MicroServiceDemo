package com.example.userservice;



import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
		"com.example.userservice", // Scans your controllers and services
		"common.multitenancy",     // Scans the MultiTenantConnectionProvider in common-lib
		"common.config"            // Scans any other common configs
})
public class UserServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}
}
