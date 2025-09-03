package com.example.marketplace.service;

import com.example.marketplace.enums.PaymentStatus;
import com.example.marketplace.model.Payment;
import com.example.marketplace.model.User;
import com.example.marketplace.repository.PaymentRepository;
import com.example.marketplace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final SubscriptionService subscriptionService;

    /**
     * Process M-Pesa payment payload
     */
    public Payment processMpesaPayment(String payload) {
        // 🔹 Parse payload from M-Pesa (JSON usually via STK Push callback)
        // Example dummy values (replace with real parsing logic)
        String transactionId = "mpesa_tx_123";  // Extract from payload JSON
        String email = "user@example.com";      // Extract from payload JSON or metadata
        double amount = 1000.0;                 // Extract from payload JSON
        PaymentStatus status = PaymentStatus.SUCCESS; // Map from payload result code

        return recordPayment(email, transactionId, amount, status);
    }

    /**
     * Record payment using Builder
     */
    public Payment recordPayment(String email, String transactionId, Double amount, PaymentStatus status) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        Payment payment = Payment.builder()
                .user(user)
                .transactionId(transactionId)
                .amount(amount)
                .status(status)
                .createdAt(LocalDateTime.now())
                .build();

        Payment saved = paymentRepository.save(payment);

        if (status == PaymentStatus.SUCCESS) {
            subscriptionService.handleSuccessfulPayment(email, saved);
        }

        return saved;
    }

    public boolean hasSuccessfulPayment(String email) {
        return paymentRepository.existsByUser_EmailAndStatus(email, PaymentStatus.SUCCESS);
    }

    public Optional<Payment> findLatestSuccessfulPayment(String email) {
        return paymentRepository.findFirstByUser_EmailAndStatusOrderByCreatedAtDesc(
                email,
                PaymentStatus.SUCCESS
        );
    }
}
