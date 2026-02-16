package notification_service.controller;


import notification_service.dto.EmailRequest;
import notification_service.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    public String sendNotification(@RequestBody EmailRequest request) {
        emailService.sendEmail(request);
        return "Notification Processed";
    }
}
