package com.rbdip.bookstore.order;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Coordinates the steps required to place an order. */
@Service
public class OrderService {

    private final OrderRequestValidator requestValidator;
    private final OrderItemResolver itemResolver;
    private final OrderPricingService pricingService;
    private final OrderPersistenceService persistenceService;
    private final OrderConfirmationNotifier confirmationNotifier;

    public OrderService(
            OrderRequestValidator requestValidator,
            OrderItemResolver itemResolver,
            OrderPricingService pricingService,
            OrderPersistenceService persistenceService,
            OrderConfirmationNotifier confirmationNotifier) {
        this.requestValidator = requestValidator;
        this.itemResolver = itemResolver;
        this.pricingService = pricingService;
        this.persistenceService = persistenceService;
        this.confirmationNotifier = confirmationNotifier;
    }

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        requestValidator.validate(request);
        List<OrderItemResolver.ResolvedOrderItem> items = itemResolver.resolve(request.items());
        BigDecimal total = pricingService.calculateTotal(
                items, request.customerType(), request.couponCode());
        Order order = persistenceService.save(request, items);
        confirmationNotifier.sendOrderConfirmation(request.customerFullName(), order.getId(), total);
        return order;
    }
}
