package notification_service.service;


import notification_service.entity.Notification;
import notification_service.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository repo;
    public NotificationService(NotificationRepository repo) { this.repo = repo; }

    public List<Notification> getAllNotifications() { return repo.findAll(); }
    public Notification getNotificationById(Long id) { return repo.findById(id).orElse(null); }
    public Notification createNotification(Notification notification) { return repo.save(notification); }
    public void deleteNotification(Long id) { repo.deleteById(id); }
}
