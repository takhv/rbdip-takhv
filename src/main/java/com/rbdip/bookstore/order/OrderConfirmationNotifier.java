package com.rbdip.bookstore.order;

import java.math.BigDecimal;
import org.springframework.stereotype.Component;

/** Sends the order confirmation through the configured notification channel. */
@Component
public class OrderConfirmationNotifier {

    public void sendOrderConfirmation(String customerName, Long orderId, BigDecimal total) {
        // The training project has no mail transport, so the side effect is emulated.
        System.out.printf(
                "[email] Dear %s, your order #%d for %s has been placed.%n",
                customerName,
                orderId,
                total);
    }
}
