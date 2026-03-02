package auth_service;
//
import logging.LoggingAspect;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

@SpringBootApplication(scanBasePackages = {"auth_service", "logging"})
@EnableFeignClients
@Import(LoggingAspect.class)
public class AuthServiceApplication {
	public static void main(String[] args) {

		SpringApplication.run(AuthServiceApplication.class, args);
	}
}