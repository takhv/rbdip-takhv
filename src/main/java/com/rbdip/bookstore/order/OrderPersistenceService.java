package com.rbdip.bookstore.order;

import com.rbdip.bookstore.customer.Customer;
import com.rbdip.bookstore.customer.CustomerRepository;
import java.util.List;
import org.springframework.stereotype.Component;

/** Persists orders, normalized customer records, and product-linked items. */
@Component
public class OrderPersistenceService {

    private static final String INITIAL_ORDER_STATUS = "new";

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderPersistenceService(
            CustomerRepository customerRepository,
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository) {
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public Order save(CreateOrderRequest request, List<OrderItemResolver.ResolvedOrderItem> items) {
        Customer candidate = new Customer(
                request.customerFullName(), request.customerAddress(), request.customerPhone());
        Customer customer = customerRepository.findByFirstNameAndLastNameAndAddressAndPhone(
                        candidate.getFirstName(),
                        candidate.getLastName(),
                        candidate.getAddress(),
                        candidate.getPhone())
                .orElseGet(() -> customerRepository.save(candidate));
        Order order = new Order(customer, INITIAL_ORDER_STATUS);
        Order savedOrder = orderRepository.save(order);

        for (OrderItemResolver.ResolvedOrderItem item : items) {
            orderItemRepository.save(new OrderItem(savedOrder, item.product(), item.quantity()));
        }
        return savedOrder;
    }
}
