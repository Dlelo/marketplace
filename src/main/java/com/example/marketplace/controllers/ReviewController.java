package com.example.marketplace.controllers;

import com.example.marketplace.dto.ReviewRequest;
import com.example.marketplace.dto.ReviewResponse;
import com.example.marketplace.enums.RevieweeType;
import com.example.marketplace.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponse> submitReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ReviewRequest request
    ) {
        ReviewResponse response = reviewService.submitReview(userDetails.getUsername(), request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/househelp/{id}")
    public ResponseEntity<List<ReviewResponse>> getHouseHelpReviews(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.getReviews(id, RevieweeType.HOUSE_HELP));
    }

    @GetMapping("/homeowner/{id}")
    public ResponseEntity<List<ReviewResponse>> getHomeOwnerReviews(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.getReviews(id, RevieweeType.HOME_OWNER));
    }

    @GetMapping("/househelp/{id}/summary")
    public ResponseEntity<Map<String, Object>> getHouseHelpRatingSummary(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.getRatingSummary(id, RevieweeType.HOUSE_HELP));
    }

    @GetMapping("/homeowner/{id}/summary")
    public ResponseEntity<Map<String, Object>> getHomeOwnerRatingSummary(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.getRatingSummary(id, RevieweeType.HOME_OWNER));
    }
}
