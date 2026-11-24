package com.rbdip.bookstore.order;

import static org.assertj.core.api.Assertions.assertThat;

import com.rbdip.bookstore.product.Product;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class OrderPricingServiceTest {

    private final OrderPricingService service = new OrderPricingService(new PricingCalculator());

    @Test
    void pricesResolvedProductsUsingTheDefaultCustomerType() {
        List<OrderItemResolver.ResolvedOrderItem> items = List.of(
                new OrderItemResolver.ResolvedOrderItem(
                        new Product("Book", new BigDecimal("50.00"), null), 2));

        BigDecimal total = service.calculateTotal(items, null, null);

        assertThat(total).isEqualByComparingTo("100.00");
    }

    @Test
    void forwardsCustomerDiscountAndCouponToTheCalculator() {
        List<OrderItemResolver.ResolvedOrderItem> items = List.of(
                new OrderItemResolver.ResolvedOrderItem(
                        new Product("Book", new BigDecimal("100.00"), null), 1));

        BigDecimal total = service.calculateTotal(items, "vip", "SAVE10");

        assertThat(total).isEqualByComparingTo("80.00");
    }
}
