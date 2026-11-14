package com.rbdip.bookstore.order;

import org.springframework.stereotype.Component;

/** Validates required order fields and normalizes requested quantities. */
@Component
public class OrderRequestValidator {

    private static final int DEFAULT_QUANTITY = 1;

    public void validate(CreateOrderRequest request) {
        requireNonBlank(request.customerFullName(), "customerFullName");
        requireNonBlank(request.customerAddress(), "customerAddress");
        if (request.items() == null || request.items().isEmpty()) {
            throw new IllegalArgumentException("order must contain at least one item");
        }
    }

    public int normalizeQuantity(Integer quantity) {
        int normalizedQuantity = quantity == null ? DEFAULT_QUANTITY : quantity;
        if (normalizedQuantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive");
        }
        return normalizedQuantity;
    }

    private void requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
    }
}
