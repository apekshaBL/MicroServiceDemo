package payment_service.controller;


import org.springframework.web.bind.annotation.*;
import payment_service.entity.Payment;
import payment_service.service.PaymentService;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService service;
    public PaymentController(PaymentService service) { this.service = service; }

    @GetMapping
    public List<Payment> getAllPayments() { return service.getAllPayments(); }

    @GetMapping("/{id}")
    public Payment getPayment(@PathVariable Long id) { return service.getPaymentById(id); }

    @PostMapping
    public Payment createPayment(@RequestBody Payment payment) { return service.createPayment(payment); }

    @DeleteMapping("/{id}")
    public void deletePayment(@PathVariable Long id) { service.deletePayment(id); }
}
