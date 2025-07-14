package com.example.marketplace.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InterviewDTO {
    private Long hireRequestId;
    private LocalDateTime scheduledTime;
    private String meetingDetails;
}