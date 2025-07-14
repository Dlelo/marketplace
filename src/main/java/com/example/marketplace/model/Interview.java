package com.example.marketplace.model;

import com.example.marketplace.enums.InterviewStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Interview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hire_request_id", nullable = false)
    private HireRequest hireRequest;

    private LocalDateTime scheduledTime;

    @Enumerated(EnumType.STRING)
    private InterviewStatus status;

    private String meetingDetails;
}