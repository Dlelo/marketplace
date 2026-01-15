package com.example.marketplace.controllers;

import com.example.marketplace.dto.ManualMpesaCallBackRequest;
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

        Payment payment = paymentRepository.findFirstByTransactionId(checkoutRequestId)
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

    @PostMapping("/manual-callback")
    public ResponseEntity<String> handleManualCallback(@RequestBody ManualMpesaCallBackRequest request) {
        // Extract CheckoutRequestID and ResultCode

        String transactionId = request.getTransactionId();

        Payment payment = paymentRepository.findFirstByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (request.getStatus() == PaymentStatus.SUCCESS) {
            payment.setStatus(PaymentStatus.SUCCESS);
            subscriptionService.handleSuccessfulPayment(payment.getUser().getEmail(), payment);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
        }

        paymentRepository.save(payment);
        return ResponseEntity.ok("Callback processed");
    }
}
