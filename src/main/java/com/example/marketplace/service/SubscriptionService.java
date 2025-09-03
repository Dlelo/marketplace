package com.example.marketplace.service;

import com.example.marketplace.model.Subscription;
import com.example.marketplace.model.Payment;
import java.time.LocalDateTime;
import com.example.marketplace.model.User;
import com.example.marketplace.repository.PaymentRepository;
import com.example.marketplace.repository.SubscriptionRepository;
import com.example.marketplace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;

    public boolean hasActiveSubscription(String email) {
        return subscriptionRepository.existsByUserEmailAndActiveTrue(email);
    }

    public Subscription handleSuccessfulPayment(String email, Payment payment) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Subscription subscription = subscriptionRepository.findByUserEmail(email)
                .orElse(new Subscription());

        subscription.setUser(user);
        subscription.setActive(true);
        subscription.setStartDate(LocalDateTime.now());
        subscription.setEndDate(LocalDateTime.now().plusDays(365));
        subscription.setLastPayment(payment);
        return subscriptionRepository.save(subscription);
    }

    public void checkAndExpireSubscription(Subscription subscription) {
        if (subscription.getEndDate().isBefore(LocalDateTime.now())) {
            subscription.setActive(false);
            subscriptionRepository.save(subscription);
        }
    }
}