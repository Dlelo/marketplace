package com.example.marketplace.controllers;

import com.example.marketplace.dto.PaymentRequest;
import com.example.marketplace.dto.PaymentResponseDTO;
import com.example.marketplace.repository.UserRepository;
import com.example.marketplace.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final UserRepository userRepository;

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
                    .body(Map.of("message", e.getMessage()));
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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/archived")
    public ResponseEntity<Page<PaymentResponseDTO>> listArchivedPayments(Pageable pageable) {
        return ResponseEntity.ok(paymentService.getArchivedPayments(pageable));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/verify")
    public ResponseEntity<PaymentResponseDTO> verifyPaymentManually(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(paymentService.verifyPaymentManually(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/archive")
    public ResponseEntity<PaymentResponseDTO> archivePayment(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(paymentService.archivePayment(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Returns the payment history for a user. Accessible to ADMIN/AGENT/SALES,
     * or to the user themselves.
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','AGENT','SALES','HOMEOWNER','HOUSEHELP')")
    public ResponseEntity<?> getPaymentsForUser(@PathVariable Long userId, Authentication auth) {
        boolean privileged = auth.getAuthorities().stream()
                .anyMatch(a -> {
                    String r = a.getAuthority();
                    return r.equals("ROLE_ADMIN") || r.equals("ROLE_AGENT") || r.equals("ROLE_SALES");
                });
        if (!privileged) {
            String email = auth.getName();
            boolean isSelf = userRepository.findById(userId)
                    .map(u -> email.equalsIgnoreCase(u.getEmail()))
                    .orElse(false);
            if (!isSelf) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        List<PaymentResponseDTO> payments = paymentService.getPaymentsForUser(userId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/status/{transactionId}")
    public ResponseEntity<?> getPaymentByTransactionId(@PathVariable String transactionId) {
        try {
            return ResponseEntity.ok(paymentService.findByTransactionId(transactionId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
