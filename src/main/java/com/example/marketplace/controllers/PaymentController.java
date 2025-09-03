package com.example.marketplace.controllers;

import com.example.marketplace.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments/webhook")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Webhook for M-Pesa payments
     */
    @PostMapping("/mpesa")
    public ResponseEntity<String> handleMpesaWebhook(@RequestBody String payload) {
        try {
            paymentService.processMpesaPayment(payload);
            return ResponseEntity.ok("M-Pesa payment processed successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("M-Pesa webhook failed: " + e.getMessage());
        }
    }
}
