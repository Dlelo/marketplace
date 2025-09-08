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
    @SuppressWarnings("unchecked")
    public void handleDarajaCallback(Map<String, Object> payload) {
        Map<String, Object> body = (Map<String, Object>) payload.get("Body");
        Map<String, Object> stkCallback = (Map<String, Object>) body.get("stkCallback");

        String checkoutRequestId = (String) stkCallback.get("CheckoutRequestID");
        int resultCode = (Integer) stkCallback.get("ResultCode");
        String resultDesc = (String) stkCallback.get("ResultDesc");

        Payment payment = paymentRepository.findByTransactionId(checkoutRequestId)
                .orElseThrow(() -> new RuntimeException("Payment not found for CheckoutRequestID: " + checkoutRequestId));

        if (resultCode == 0) {
            // Extract metadata
            Map<String, Object> callbackMetadata = (Map<String, Object>) stkCallback.get("CallbackMetadata");
            if (callbackMetadata != null) {
                var items = (Iterable<Map<String, Object>>) callbackMetadata.get("Item");
                for (Map<String, Object> item : items) {
                    String name = (String) item.get("Name");
                    Object value = item.get("Value");

                    if ("MpesaReceiptNumber".equals(name)) {
                        payment.setTransactionId(value.toString());
                    }
                    if ("Amount".equals(name)) {
                        payment.setAmount(Double.valueOf(value.toString()));
                    }
                }
            }

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
