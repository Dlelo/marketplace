package com.example.marketplace.repository;

import com.example.marketplace.enums.PaymentStatus;
import com.example.marketplace.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    boolean existsByUser_EmailAndStatus(String email, PaymentStatus status);

    // Get the most recent successful payment for a user
    Optional<Payment> findFirstByUser_EmailAndStatusOrderByCreatedAtDesc(String email, PaymentStatus status);
}
