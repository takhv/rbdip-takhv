package com.rbdip.bookstore.order;

import java.util.List;
import org.springframework.stereotype.Component;

/** Persists the order and its item snapshots. */
@Component
public class OrderPersistenceService {

    private static final String INITIAL_ORDER_STATUS = "new";

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderPersistenceService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public Order save(CreateOrderRequest request, List<OrderItemResolver.ResolvedOrderItem> items) {
        Order order = new Order(
                request.customerFullName(),
                request.customerAddress(),
                request.customerPhone(),
                INITIAL_ORDER_STATUS);
        Order savedOrder = orderRepository.save(order);

        for (OrderItemResolver.ResolvedOrderItem item : items) {
            orderItemRepository.save(new OrderItem(
                    savedOrder.getId(),
                    item.product().getName(),
                    item.product().getPrice(),
                    item.quantity()));
        }
        return savedOrder;
    }
}
