package com.rbdip.bookstore.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rbdip.bookstore.customer.Customer;
import com.rbdip.bookstore.customer.CustomerRepository;
import com.rbdip.bookstore.product.Product;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class OrderPersistenceServiceTest {

    private final CustomerRepository customerRepository = mock(CustomerRepository.class);
    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final OrderItemRepository orderItemRepository = mock(OrderItemRepository.class);
    private final OrderPersistenceService service = new OrderPersistenceService(
            customerRepository, orderRepository, orderItemRepository);

    @Test
    void createsCustomerAndPersistsOrderItems() {
        CreateOrderRequest request = request("Ivan Petrov", "Moscow", "+7000");
        Product product = new Product("Book", new BigDecimal("12.50"), null);
        when(customerRepository.findByFirstNameAndLastNameAndAddressAndPhone(
                        "Ivan", "Petrov", "Moscow", "+7000"))
                .thenReturn(Optional.empty());
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order order = service.save(request, List.of(new OrderItemResolver.ResolvedOrderItem(product, 2)));

        assertThat(order.getCustomerFullName()).isEqualTo("Ivan Petrov");
        assertThat(order.getStatus()).isEqualTo("new");
        verify(customerRepository).save(any(Customer.class));
        verify(orderItemRepository).save(any(OrderItem.class));
    }

    @Test
    void reusesAnExistingCustomer() {
        CreateOrderRequest request = request("Ivan Petrov", "Moscow", "+7000");
        Customer existingCustomer = new Customer("Ivan Petrov", "Moscow", "+7000");
        when(customerRepository.findByFirstNameAndLastNameAndAddressAndPhone(
                        "Ivan", "Petrov", "Moscow", "+7000"))
                .thenReturn(Optional.of(existingCustomer));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order order = service.save(request, List.of());

        assertThat(order.getCustomer()).isSameAs(existingCustomer);
        verify(customerRepository, org.mockito.Mockito.never()).save(any(Customer.class));
    }

    private CreateOrderRequest request(String name, String address, String phone) {
        return new CreateOrderRequest(name, address, phone, "regular", null, List.of());
    }
}
