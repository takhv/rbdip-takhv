package com.rbdip.bookstore.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.rbdip.bookstore.product.Product;
import com.rbdip.bookstore.product.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class OrderItemResolverTest {

    private final ProductRepository productRepository = mock(ProductRepository.class);
    private final OrderItemResolver resolver = new OrderItemResolver(
            productRepository, new OrderRequestValidator());

    @Test
    void resolvesProductsAndDefaultsMissingQuantities() {
        Product product = new Product("Book", new BigDecimal("12.50"), null);
        when(productRepository.findById(7L)).thenReturn(Optional.of(product));

        List<OrderItemResolver.ResolvedOrderItem> items = resolver.resolve(
                List.of(new CreateOrderRequest.Item(7L, null)));

        assertThat(items).hasSize(1);
        assertThat(items.get(0).product()).isSameAs(product);
        assertThat(items.get(0).quantity()).isEqualTo(1);
    }

    @Test
    void rejectsMissingProductsAndInvalidQuantities() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());
        Product product = new Product("Book", new BigDecimal("12.50"), null);
        when(productRepository.findById(7L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> resolver.resolve(List.of(new CreateOrderRequest.Item(99L, 1))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("product 99 not found");
        assertThatThrownBy(() -> resolver.resolve(List.of(new CreateOrderRequest.Item(7L, 0))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("quantity must be positive");
    }
}
