package com.example.marketplace.controllers;

import com.example.marketplace.dto.PaymentRequest;
import com.example.marketplace.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Initiate M-Pesa payment
     */
    @PostMapping("/initiate")
    public ResponseEntity<Map<String, Object>> initiatePayment(@RequestBody PaymentRequest request) {
        try {
            Map<String, Object> response = paymentService.initiatePayment(
                    request.getPhoneNumber(),
                    request.getAmount(),
                    request.getEmail()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Webhook for M-Pesa payments
     */
    @PostMapping("/webhook/mpesa")
    public ResponseEntity<String> handleMpesaWebhook(@RequestBody Map<String, Object> payload) {
        try {
            paymentService.handleDarajaCallback(payload);
            return ResponseEntity.ok("M-Pesa payment processed successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("M-Pesa webhook failed: " + e.getMessage());
        }
    }
}
