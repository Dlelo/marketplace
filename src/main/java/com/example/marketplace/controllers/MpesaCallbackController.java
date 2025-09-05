package com.example.marketplace.controllers;

import com.example.marketplace.enums.PaymentStatus;
import com.example.marketplace.model.Payment;
import com.example.marketplace.repository.PaymentRepository;
import com.example.marketplace.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/mpesa")
@RequiredArgsConstructor
public class MpesaCallbackController {

    private final PaymentRepository paymentRepository;
    private final SubscriptionService subscriptionService;

    @PostMapping("/callback")
    public ResponseEntity<String> handleCallback(@RequestBody Map<String, Object> payload) {
        // Extract CheckoutRequestID and ResultCode
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
        return ResponseEntity.ok("Callback processed");
    }
}
