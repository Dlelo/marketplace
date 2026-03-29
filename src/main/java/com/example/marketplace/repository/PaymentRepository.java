package com.example.marketplace.repository;

import com.example.marketplace.enums.PaymentStatus;
import com.example.marketplace.model.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    boolean existsByUser_EmailAndStatus(String email, PaymentStatus status);

    Optional<Payment> findFirstByUser_EmailAndStatusOrderByCreatedAtDesc(String email, PaymentStatus status);

    Optional<Payment> findFirstByTransactionId(String transactionId);

    Page<Payment> findAllByArchivedFalse(Pageable pageable);

    Page<Payment> findAllByArchivedTrue(Pageable pageable);

    long countByStatus(PaymentStatus status);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'SUCCESS'")
    double sumRevenue();

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'SUCCESS' AND p.createdAt >= :since")
    double sumRevenueSince(@Param("since") LocalDateTime since);

    long countByStatusAndCreatedAtAfter(PaymentStatus status, LocalDateTime since);
}
