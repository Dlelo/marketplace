package com.example.marketplace.dto;

import com.example.marketplace.enums.RevieweeType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewResponse {
    private Long id;
    private String reviewerName;
    private Long revieweeId;
    private RevieweeType revieweeType;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
