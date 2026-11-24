package com.rbdip.bookstore.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

class OrderServiceTest {

    private final OrderRequestValidator requestValidator = mock(OrderRequestValidator.class);
    private final OrderItemResolver itemResolver = mock(OrderItemResolver.class);
    private final OrderPricingService pricingService = mock(OrderPricingService.class);
    private final OrderPersistenceService persistenceService = mock(OrderPersistenceService.class);
    private final OrderConfirmationNotifier confirmationNotifier = mock(OrderConfirmationNotifier.class);
    private final OrderService service = new OrderService(
            requestValidator, itemResolver, pricingService, persistenceService, confirmationNotifier);

    @Test
    void coordinatesValidationPricingPersistenceAndConfirmation() {
        CreateOrderRequest request = new CreateOrderRequest(
                "Ivan Petrov", "Moscow", null, "vip", "SAVE10",
                List.of(new CreateOrderRequest.Item(1L, 2)));
        List<OrderItemResolver.ResolvedOrderItem> items = List.of();
        Order order = mock(Order.class);
        when(itemResolver.resolve(request.items())).thenReturn(items);
        when(pricingService.calculateTotal(items, "vip", "SAVE10")).thenReturn(new BigDecimal("80.00"));
        when(persistenceService.save(request, items)).thenReturn(order);
        when(order.getId()).thenReturn(25L);

        Order result = service.createOrder(request);

        assertThat(result).isSameAs(order);
        InOrder calls = inOrder(requestValidator, itemResolver, pricingService, persistenceService, confirmationNotifier);
        calls.verify(requestValidator).validate(request);
        calls.verify(itemResolver).resolve(request.items());
        calls.verify(pricingService).calculateTotal(items, "vip", "SAVE10");
        calls.verify(persistenceService).save(request, items);
        calls.verify(confirmationNotifier).sendOrderConfirmation(
                "Ivan Petrov", 25L, new BigDecimal("80.00"));
    }
}
