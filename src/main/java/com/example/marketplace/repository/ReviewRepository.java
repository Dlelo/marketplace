package com.example.marketplace.repository;

import com.example.marketplace.enums.RevieweeType;
import com.example.marketplace.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByRevieweeIdAndRevieweeTypeOrderByCreatedAtDesc(Long revieweeId, RevieweeType revieweeType);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.revieweeId = :revieweeId AND r.revieweeType = :revieweeType")
    Double averageRatingByRevieweeIdAndRevieweeType(
            @Param("revieweeId") Long revieweeId,
            @Param("revieweeType") RevieweeType revieweeType
    );

    boolean existsByReviewer_IdAndRevieweeIdAndRevieweeType(Long reviewerId, Long revieweeId, RevieweeType revieweeType);

    long countByRevieweeIdAndRevieweeType(Long revieweeId, RevieweeType revieweeType);
}
