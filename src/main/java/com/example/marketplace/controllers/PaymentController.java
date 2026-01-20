package com.example.marketplace.controllers;

import com.example.marketplace.dto.PaymentRequest;
import com.example.marketplace.dto.PaymentResponseDTO;
import com.example.marketplace.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("")
    public ResponseEntity<Page<PaymentResponseDTO>> listPayments(Pageable pageable) {
        return ResponseEntity.ok(paymentService.getAllPayments(pageable));
    }

    @GetMapping("/status/{transactionId}")
    public ResponseEntity<?> verifyPayment(@PathVariable String transactionId) {
        try{
            PaymentResponseDTO payment = paymentService.findByTransactionId(transactionId);
            return ResponseEntity.ok(payment);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

    }
}
