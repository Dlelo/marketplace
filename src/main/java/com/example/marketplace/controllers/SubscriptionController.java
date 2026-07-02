package com.example.marketplace.controllers;

import com.example.marketplace.model.Subscription;
import com.example.marketplace.model.User;
import com.example.marketplace.repository.PaymentRepository;
import com.example.marketplace.repository.SubscriptionRepository;
import com.example.marketplace.repository.UserRepository;
import com.example.marketplace.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/subscription")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;

    @GetMapping("/status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getStatus(Authentication auth) {
        String identifier = auth.getName();
        boolean active = subscriptionRepository.hasActiveSubscription(identifier);

        Optional<Subscription> sub = subscriptionRepository.findByUserEmail(identifier);
        if (sub.isPresent() && active) {
            Subscription s = sub.get();
            return ResponseEntity.ok(Map.of(
                    "active", true,
                    "plan", s.getPlan() != null ? s.getPlan() : "HOMEOWNER_PLUS",
                    "endDate", s.getEndDate().toString()
            ));
        }
        return ResponseEntity.ok(Map.of("active", false));
    }

    @PostMapping("/activate-homeowner-plus")
    @PreAuthorize("hasAnyRole('HOMEOWNER','ADMIN')")
    public ResponseEntity<?> activateHomeownerPlus(Authentication auth) {
        String identifier = auth.getName();
        User user = userRepository.findByEmail(identifier)
                .or(() -> userRepository.findByPhoneNumber(identifier))
                .orElseThrow(() -> new RuntimeException("User not found"));

        Subscription subscription = subscriptionRepository.findByUserEmail(identifier)
                .orElse(new Subscription());

        subscription.setUser(user);
        subscription.setPlan("HOMEOWNER_PLUS");
        subscription.setAmount(999.0);
        subscription.setActive(true);
        subscription.setStartDate(LocalDateTime.now());
        subscription.setEndDate(LocalDateTime.now().plusDays(365));

        subscriptionRepository.save(subscription);

        return ResponseEntity.ok(Map.of(
                "active", true,
                "plan", "HOMEOWNER_PLUS",
                "endDate", subscription.getEndDate().toString()
        ));
    }
}
