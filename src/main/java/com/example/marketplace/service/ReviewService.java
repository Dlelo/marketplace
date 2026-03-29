package com.example.marketplace.service;

import com.example.marketplace.dto.ReviewRequest;
import com.example.marketplace.dto.ReviewResponse;
import com.example.marketplace.enums.RevieweeType;
import com.example.marketplace.model.Review;
import com.example.marketplace.model.User;
import com.example.marketplace.repository.ReviewRepository;
import com.example.marketplace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public ReviewResponse submitReview(String username, ReviewRequest request) {
        User reviewer = userRepository.findByEmail(username)
                .or(() -> userRepository.findByPhoneNumber(username))
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (reviewRepository.existsByReviewer_IdAndRevieweeIdAndRevieweeType(
                reviewer.getId(), request.getRevieweeId(), request.getRevieweeType())) {
            throw new RuntimeException("You have already submitted a review for this person.");
        }

        Review review = new Review();
        review.setReviewer(reviewer);
        review.setRevieweeId(request.getRevieweeId());
        review.setRevieweeType(request.getRevieweeType());
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review saved = reviewRepository.save(review);
        return toResponse(saved);
    }

    public List<ReviewResponse> getReviews(Long revieweeId, RevieweeType revieweeType) {
        return reviewRepository
                .findByRevieweeIdAndRevieweeTypeOrderByCreatedAtDesc(revieweeId, revieweeType)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public Map<String, Object> getRatingSummary(Long revieweeId, RevieweeType revieweeType) {
        Double avg = reviewRepository.averageRatingByRevieweeIdAndRevieweeType(revieweeId, revieweeType);
        long count = reviewRepository.countByRevieweeIdAndRevieweeType(revieweeId, revieweeType);
        double rounded = avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;
        return Map.of("average", rounded, "count", count);
    }

    private ReviewResponse toResponse(Review review) {
        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());
        response.setReviewerName(review.getReviewer().getName());
        response.setRevieweeId(review.getRevieweeId());
        response.setRevieweeType(review.getRevieweeType());
        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setCreatedAt(review.getCreatedAt());
        return response;
    }
}
