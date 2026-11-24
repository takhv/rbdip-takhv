package com.rbdip.bookstore.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;

class OrderRequestValidatorTest {

    private final OrderRequestValidator validator = new OrderRequestValidator();

    @Test
    void acceptsACompleteRequest() {
        CreateOrderRequest request = new CreateOrderRequest(
                "Ivan Petrov", "Moscow", null, "regular", null,
                List.of(new CreateOrderRequest.Item(1L, 1)));

        validator.validate(request);
    }

    @Test
    void rejectsMissingCustomerFieldsAndItems() {
        assertThatThrownBy(() -> validator.validate(
                new CreateOrderRequest(" ", "Moscow", null, null, null, List.of())))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("customerFullName is required");
        assertThatThrownBy(() -> validator.validate(
                new CreateOrderRequest("Ivan", " ", null, null, null, List.of())))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("customerAddress is required");
        assertThatThrownBy(() -> validator.validate(
                new CreateOrderRequest("Ivan", "Moscow", null, null, null, null)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("order must contain at least one item");
    }

    @Test
    void defaultsMissingQuantityAndRejectsNonPositiveValues() {
        assertThat(validator.normalizeQuantity(null)).isEqualTo(1);
        assertThat(validator.normalizeQuantity(3)).isEqualTo(3);
        assertThatThrownBy(() -> validator.normalizeQuantity(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("quantity must be positive");
        assertThatThrownBy(() -> validator.normalizeQuantity(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
