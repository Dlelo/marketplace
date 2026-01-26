package com.example.marketplace.model;

import com.example.marketplace.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private Double amount;
    private Double baseFee;             // Original plan fee (500, 2500, 2500)
    private Double surchargeFee;        // County surcharge amount
    private String surchargeReason;

    private String provider; // M-PESA

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private LocalDateTime createdAt;
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    /**
     * Check if payment has surcharge
     */
    public boolean hasSurcharge() {
        return surchargeFee != null && surchargeFee > 0;
    }

    /**
     * Get surcharge percentage of total
     */
//    public double getSurchargePercentage() {
//        if (amount == null || amount == 0) {
//            return 0;
//        }
//        return (surchargeFee != null ? surchargeFee : 0) / amount * 100;
//    }
}
