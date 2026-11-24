package com.rbdip.bookstore.order;

import com.rbdip.bookstore.review.PurchaseVerifier;
import org.springframework.stereotype.Component;

/** Adapter that answers the review module's purchase-history request. */
@Component
public class OrderPurchaseVerifier implements PurchaseVerifier {

    private final OrderItemRepository orderItemRepository;

    public OrderPurchaseVerifier(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public boolean wasProductPurchased(Long productId) {
        return orderItemRepository.existsByProduct_Id(productId);
    }
}
