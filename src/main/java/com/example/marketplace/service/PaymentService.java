package com.example.marketplace.service;

import com.example.marketplace.enums.PaymentStatus;
import com.example.marketplace.model.Payment;
import com.example.marketplace.model.User;
import com.example.marketplace.repository.PaymentRepository;
import com.example.marketplace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final DarajaService darajaService;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final SubscriptionService subscriptionService;

    /**
     * Initiates M-Pesa STK Push payment
     */
    public Map<String, Object> initiatePayment(String phoneNumber, double amount, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        String accountReference = "Subscription-" + email;
        String transactionDesc = "Subscription Payment";

        Map<String, Object> response = darajaService.lipaNaMpesa(phoneNumber, amount, accountReference, transactionDesc);

        // Save pending payment
        Payment payment = Payment.builder()
                .user(user)
                .transactionId(response.get("CheckoutRequestID").toString())
                .amount(amount)
                .status(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        paymentRepository.save(payment);

        return response;
    }

    /**
     * Process callback from Daraja
     */
    public void handleDarajaCallback(Map<String, Object> payload) {
        Map<String, Object> stkCallback = (Map<String, Object>) payload.get("Body");
        Map<String, Object> callback = (Map<String, Object>) stkCallback.get("stkCallback");

        String checkoutRequestId = (String) callback.get("CheckoutRequestID");
        int resultCode = (Integer) callback.get("ResultCode");

        Payment payment = paymentRepository.findByTransactionId(checkoutRequestId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (resultCode == 0) {
            payment.setStatus(PaymentStatus.SUCCESS);
            subscriptionService.handleSuccessfulPayment(payment.getUser().getEmail(), payment);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
        }

        paymentRepository.save(payment);
    }

    public boolean hasSuccessfulPayment(String email) {
        return paymentRepository.existsByUser_EmailAndStatus(email, PaymentStatus.SUCCESS);
    }

    public Optional<Payment> findLatestSuccessfulPayment(String email) {
        return paymentRepository.findFirstByUser_EmailAndStatusOrderByCreatedAtDesc(email, PaymentStatus.SUCCESS);
    }
}
