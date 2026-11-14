package com.rbdip.bookstore.order;

import com.rbdip.bookstore.product.Product;
import com.rbdip.bookstore.product.ProductRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/** Resolves requested product identifiers and validates their quantities. */
@Component
public class OrderItemResolver {

    private final ProductRepository productRepository;
    private final OrderRequestValidator requestValidator;

    public OrderItemResolver(
            ProductRepository productRepository,
            OrderRequestValidator requestValidator) {
        this.productRepository = productRepository;
        this.requestValidator = requestValidator;
    }

    public List<ResolvedOrderItem> resolve(List<CreateOrderRequest.Item> requestedItems) {
        List<ResolvedOrderItem> resolvedItems = new ArrayList<>();
        for (CreateOrderRequest.Item requestedItem : requestedItems) {
            Product product = productRepository.findById(requestedItem.productId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "product " + requestedItem.productId() + " not found"));
            int quantity = requestValidator.normalizeQuantity(requestedItem.quantity());
            resolvedItems.add(new ResolvedOrderItem(product, quantity));
        }
        return List.copyOf(resolvedItems);
    }

    public record ResolvedOrderItem(Product product, int quantity) {
    }
}
