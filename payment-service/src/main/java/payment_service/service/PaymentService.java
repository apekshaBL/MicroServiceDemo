package payment_service.service;


import org.springframework.stereotype.Service;
import payment_service.entity.Payment;
import payment_service.repository.PaymentRepository;

import java.util.List;

@Service
public class PaymentService {
    private final PaymentRepository repo;
    public PaymentService(PaymentRepository repo) { this.repo = repo; }

    public List<Payment> getAllPayments() { return repo.findAll(); }
    public Payment getPaymentById(Long id) { return repo.findById(id).orElse(null); }
    public Payment createPayment(Payment payment) { return repo.save(payment); }
    public void deletePayment(Long id) { repo.deleteById(id); }
}
