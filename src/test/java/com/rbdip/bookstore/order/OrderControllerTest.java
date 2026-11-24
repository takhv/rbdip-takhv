package com.rbdip.bookstore.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class OrderControllerTest {

    private final OrderService orderService = mock(OrderService.class);
    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final OrderController controller = new OrderController(orderService, orderRepository);

    @Test
    void returnsTheCreatedOrderIdentifierAndStatus() {
        CreateOrderRequest request = new CreateOrderRequest("Ivan", "Moscow", null, null, null, List.of());
        Order order = mock(Order.class);
        when(orderService.createOrder(request)).thenReturn(order);
        when(order.getId()).thenReturn(42L);
        when(order.getStatus()).thenReturn("new");

        Map<String, Object> response = controller.createOrder(request);

        assertThat(response).containsEntry("id", 42L).containsEntry("status", "new");
    }

    @Test
    void listsOrdersWithTheirFetchedItems() {
        Order order = mock(Order.class);
        OrderItem item = mock(OrderItem.class);
        when(orderRepository.findAllWithCustomerAndItems()).thenReturn(List.of(order));
        when(order.getId()).thenReturn(7L);
        when(order.getCustomerFullName()).thenReturn("Ivan Petrov");
        when(order.getStatus()).thenReturn("new");
        when(order.getItems()).thenReturn(List.of(item));
        when(item.getProductName()).thenReturn("Book");
        when(item.getQuantity()).thenReturn(2);

        List<Map<String, Object>> response = controller.listOrders();

        assertThat(response).hasSize(1);
        assertThat(response.get(0)).containsEntry("customerFullName", "Ivan Petrov");
        assertThat((List<?>) response.get(0).get("items")).hasSize(1);
    }
}
