package com.example.marketplace.model;
import com.example.marketplace.enums.RequestStatus;
import jakarta.persistence.*;


import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class HireRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User buyer;

    @ManyToOne
    private User seller;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    private LocalDate startDate;

    private String message;

    private LocalDateTime createdAt = LocalDateTime.now();
}
