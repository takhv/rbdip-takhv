package com.rbdip.bookstore;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class GlobalExceptionHandlerTest {

    @Test
    void mapsValidationFailuresToBadRequest() {
        var response = new GlobalExceptionHandler().handleValidation(
                new IllegalArgumentException("invalid order"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(Map.of("error", "invalid order"));
    }
}
