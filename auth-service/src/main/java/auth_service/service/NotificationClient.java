package auth_service.service;

import auth_service.dto.EmailRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "notification-service", url = "http://localhost:8091")
public interface NotificationClient {
    @PostMapping("/notification/send")
    void sendEmail(@RequestBody EmailRequest request);
}