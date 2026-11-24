package com.rbdip.bookstore.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ProductControllerTest {

    private final ProductRepository productRepository = mock(ProductRepository.class);
    private final ProductController controller = new ProductController(productRepository);

    @Test
    void createsAProductFromTheRequestBody() {
        Product savedProduct = mock(Product.class);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
        when(savedProduct.getId()).thenReturn(3L);
        when(savedProduct.getName()).thenReturn("Book");

        Map<String, Object> response = controller.createProduct(
                Map.of("name", "Book", "price", "12.50", "description", "Novel"));

        assertThat(response).containsEntry("id", 3L).containsEntry("name", "Book");
    }

    @Test
    void listsProductsWithTheirPrices() {
        Product product = mock(Product.class);
        when(productRepository.findAll()).thenReturn(List.of(product));
        when(product.getId()).thenReturn(3L);
        when(product.getName()).thenReturn("Book");
        when(product.getPrice()).thenReturn(new BigDecimal("12.50"));

        List<Map<String, Object>> response = controller.listProducts();

        assertThat(response).containsExactly(Map.of("id", 3L, "name", "Book", "price", "12.50"));
    }
}
