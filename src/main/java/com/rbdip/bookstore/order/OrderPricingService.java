package com.rbdip.bookstore.order;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Component;

/** Builds the pricing input from resolved products and calculates the total. */
@Component
public class OrderPricingService {

    private static final String DEFAULT_CUSTOMER_TYPE = "regular";

    private final PricingCalculator pricingCalculator;

    public OrderPricingService(PricingCalculator pricingCalculator) {
        this.pricingCalculator = pricingCalculator;
    }

    public BigDecimal calculateTotal(
            List<OrderItemResolver.ResolvedOrderItem> items,
            String customerType,
            String couponCode) {
        List<PricingCalculator.LineItem> lineItems = items.stream()
                .map(item -> new PricingCalculator.LineItem(item.product().getPrice(), item.quantity()))
                .toList();
        String effectiveCustomerType = customerType == null ? DEFAULT_CUSTOMER_TYPE : customerType;
        return pricingCalculator.calculateOrderTotal(lineItems, effectiveCustomerType, couponCode);
    }
}
