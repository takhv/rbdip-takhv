package com.rbdip.bookstore.order;

import static org.assertj.core.api.Assertions.assertThat;

import com.rbdip.bookstore.customer.Customer;
import com.rbdip.bookstore.product.Product;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class OrderEntityTest {

    @Test
    void exposesNormalizedOrderAndProductAssociations() {
        Customer customer = new Customer("Ivan Petrov", "Moscow", null);
        Order order = new Order(customer, "new");
        Product product = new Product("Book", new BigDecimal("15.00"), "Novel");
        OrderItem item = new OrderItem(order, product, 2);

        assertThat(order.getCustomer()).isSameAs(customer);
        assertThat(order.getCustomerFullName()).isEqualTo("Ivan Petrov");
        assertThat(order.getStatus()).isEqualTo("new");
        assertThat(order.getId()).isNull();
        assertThat(order.getCreatedAt()).isNotNull();
        assertThat(order.getItems()).isEmpty();
        assertThat(item.getOrder()).isSameAs(order);
        assertThat(item.getProduct()).isSameAs(product);
        assertThat(item.getProductName()).isEqualTo("Book");
        assertThat(item.getQuantity()).isEqualTo(2);
        assertThat(item.getId()).isNull();
        assertThat(product.getId()).isNull();
        assertThat(product.getDescription()).isEqualTo("Novel");
    }
}
