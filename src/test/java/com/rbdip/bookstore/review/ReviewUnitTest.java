package com.rbdip.bookstore.review;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.rbdip.bookstore.order.OrderItemRepository;
import com.rbdip.bookstore.order.OrderPurchaseVerifier;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ReviewUnitTest {

    private final ReviewRepository reviewRepository = mock(ReviewRepository.class);
    private final PurchaseVerifier purchaseVerifier = mock(PurchaseVerifier.class);
    private final ReviewService reviewService = new ReviewService(reviewRepository, purchaseVerifier);
    private final ReviewController controller = new ReviewController(reviewService);

    @Test
    void createsReviewsEvenWhenPurchaseIsUnverified() {
        when(purchaseVerifier.wasProductPurchased(4L)).thenReturn(false);
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Review review = reviewService.addReview(4L, null, 5, "Good book");

        assertThat(review.getProductId()).isEqualTo(4L);
        assertThat(review.getAuthorName()).isEqualTo("anonymous");
        assertThat(review.getRating()).isEqualTo(5);
        assertThat(review.getComment()).isEqualTo("Good book");
        assertThat(review.getId()).isNull();
    }

    @Test
    void listsReviewsForAProduct() {
        Review review = new Review(4L, "Ivan", 4, "Useful");
        when(reviewRepository.findByProductId(4L)).thenReturn(List.of(review));

        assertThat(reviewService.listReviews(4L)).containsExactly(review);
    }

    @Test
    void controllerMapsReviewCreateAndListResponses() {
        Review savedReview = mock(Review.class);
        when(savedReview.getId()).thenReturn(11L);
        when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);
        Map<String, Object> body = Map.of("authorName", "Ivan", "rating", 5, "comment", "Great");

        Map<String, Object> created = controller.addReview(4L, body);
        List<Map<String, Object>> listed = controller.listReviews(4L);

        assertThat(created).containsEntry("id", 11L);
        assertThat(listed).isEmpty();
    }

    @Test
    void controllerReturnsEmptyTextForNullComments() {
        Review review = new Review(4L, "Ivan", 4, null);
        when(reviewRepository.findByProductId(4L)).thenReturn(List.of(review));

        List<Map<String, Object>> response = controller.listReviews(4L);

        assertThat(response).containsExactly(Map.of("authorName", "Ivan", "rating", 4, "comment", ""));
    }

    @Test
    void orderAdapterUsesThePurchasePort() {
        OrderItemRepository orderItemRepository = mock(OrderItemRepository.class);
        when(orderItemRepository.existsByProduct_Id(4L)).thenReturn(true);

        assertThat(new OrderPurchaseVerifier(orderItemRepository).wasProductPurchased(4L)).isTrue();
    }
}
