package com.example.marketplace.model;
import com.example.marketplace.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class HireRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User homeOwner;

    @ManyToOne
    private User houseHelp;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    private LocalDate startDate;

    private String message;

    private LocalDateTime createdAt = LocalDateTime.now();
}
