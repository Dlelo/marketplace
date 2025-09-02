package com.example.marketplace.repository;

import com.example.marketplace.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findByUserEmail(String email);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " +
            "FROM Subscription s " +
            "WHERE s.user.email = :email " +
            "AND s.active = true " +
            "AND (s.endDate IS NULL OR s.endDate >= CURRENT_DATE)")
    boolean hasActiveSubscription(String email);

    boolean existsByUserEmailAndActiveTrue(String email);
}
