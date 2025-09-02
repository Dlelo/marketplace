package com.example.marketplace.service;

import com.example.marketplace.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;

    public boolean hasActiveSubscription(String email) {
        return subscriptionRepository.existsByUserEmailAndActiveTrue(email);
    }
}