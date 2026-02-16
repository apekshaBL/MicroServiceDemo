package notification_service.service;

import notification_service.dto.EmailRequest;
import notification_service.entity.Notification;
import notification_service.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Autowired
    private NotificationRepository repository;

    public void sendEmail(EmailRequest request) {
        // 1. Prepare Log Entity
        Notification log = new Notification();
        log.setRecipient(request.getTo());
        log.setSubject(request.getSubject());
        log.setBody(request.getBody());
        log.setTenantId(request.getTenantId());
        log.setSentAt(LocalDateTime.now());

        // 2. Try to Send
        if (mailSender == null) {
            System.out.println("⚠️ SMTP NOT CONFIGURED. LOGGING EMAIL ONLY.");
            log.setStatus("SKIPPED_NO_SMTP");
        } else {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(request.getTo());
                message.setSubject(request.getSubject());
                message.setText(request.getBody());
                mailSender.send(message);
                log.setStatus("SENT");
                System.out.println("✅ Email sent to " + request.getTo());
            } catch (Exception e) {
                System.err.println("❌ Failed to send: " + e.getMessage());
                log.setStatus("FAILED");
            }
        }

        // 3. Save to DB
        repository.save(log);
    }
}