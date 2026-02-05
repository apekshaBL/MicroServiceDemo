package notification_service.controller;


import notification_service.entity.Notification;
import notification_service.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService service;
    public NotificationController(NotificationService service) { this.service = service; }

    @GetMapping
    public List<Notification> getAllNotifications() { return service.getAllNotifications(); }

    @GetMapping("/{id}")
    public Notification getNotification(@PathVariable Long id) { return service.getNotificationById(id); }

    @PostMapping
    public Notification createNotification(@RequestBody Notification notification) { return service.createNotification(notification); }

    @DeleteMapping("/{id}")
    public void deleteNotification(@PathVariable Long id) { service.deleteNotification(id); }
}
