package com.rbdip.bookstore.order;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class OrderConfirmationNotifierTest {

    @Test
    void printsTheOrderConfirmation() {
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (PrintStream capturedOutput = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setOut(capturedOutput);
            new OrderConfirmationNotifier().sendOrderConfirmation("Ivan", 12L, new BigDecimal("30.00"));
        } finally {
            System.setOut(originalOutput);
        }

        assertThat(output.toString(StandardCharsets.UTF_8))
                .isEqualTo("[email] Dear Ivan, your order #12 for 30.00 has been placed." + System.lineSeparator());
    }
}
