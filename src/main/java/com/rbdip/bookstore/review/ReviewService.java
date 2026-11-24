package com.rbdip.bookstore.review;

import java.util.List;
import org.springframework.stereotype.Service;

/** Review use cases depend on the module's purchase-verification contract. */
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final PurchaseVerifier purchaseVerifier;

    public ReviewService(ReviewRepository reviewRepository, PurchaseVerifier purchaseVerifier) {
        this.reviewRepository = reviewRepository;
        this.purchaseVerifier = purchaseVerifier;
    }

    public Review addReview(Long productId, String authorName, Integer rating, String comment) {
        // Purchase status is queried through the review-owned port; it does not gate review creation.
        purchaseVerifier.wasProductPurchased(productId);
        Review review = new Review(productId, authorName == null ? "anonymous" : authorName, rating, comment);
        return reviewRepository.save(review);
    }

    public List<Review> listReviews(Long productId) {
        return reviewRepository.findByProductId(productId);
    }
}
