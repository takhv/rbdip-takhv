package com.rbdip.bookstore.order;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class PricingCalculatorTest {

    private final PricingCalculator calculator = new PricingCalculator();

    @Test
    void calculatesMultipleLinesAndRoundsHalfUp() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(lineItem("10.005", 1), lineItem("2.50", 2)), "regular", null);

        assertThat(total).isEqualByComparingTo("15.01");
    }

    @Test
    void appliesBulkDiscountOnlyAboveTenItems() {
        BigDecimal atThreshold = calculator.calculateOrderTotal(
                List.of(lineItem("10.00", 10)), "regular", null);
        BigDecimal aboveThreshold = calculator.calculateOrderTotal(
                List.of(lineItem("10.00", 11)), "regular", null);

        assertThat(atThreshold).isEqualByComparingTo("100.00");
        assertThat(aboveThreshold).isEqualByComparingTo("104.50");
    }

    @Test
    void appliesTheDiscountForEachCustomerType() {
        BigDecimal vipTotal = calculator.calculateOrderTotal(
                List.of(lineItem("100.00", 1)), "vip", null);
        BigDecimal wholesaleTotal = calculator.calculateOrderTotal(
                List.of(lineItem("100.00", 1)), "wholesale", null);
        BigDecimal regularTotal = calculator.calculateOrderTotal(
                List.of(lineItem("100.00", 1)), "regular", null);

        assertThat(vipTotal).isEqualByComparingTo("90.00");
        assertThat(wholesaleTotal).isEqualByComparingTo("85.00");
        assertThat(regularTotal).isEqualByComparingTo("100.00");
    }

    @Test
    void appliesFixedAndPercentageCoupons() {
        BigDecimal fixedCouponTotal = calculator.calculateOrderTotal(
                List.of(lineItem("100.00", 1)), "regular", "SAVE10");
        BigDecimal percentageCouponTotal = calculator.calculateOrderTotal(
                List.of(lineItem("100.00", 1)), "regular", "SAVE20PERCENT");
        BigDecimal unknownCouponTotal = calculator.calculateOrderTotal(
                List.of(lineItem("100.00", 1)), "regular", "UNKNOWN");

        assertThat(fixedCouponTotal).isEqualByComparingTo("90.00");
        assertThat(percentageCouponTotal).isEqualByComparingTo("80.00");
        assertThat(unknownCouponTotal).isEqualByComparingTo("100.00");
    }

    @Test
    void doesNotAllowAFixedCouponToMakeTheTotalNegative() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(lineItem("5.00", 1)), "regular", "SAVE10");

        assertThat(total).isEqualByComparingTo("0.00");
    }

    @Test
    void appliesHighValueDiscountOnlyAboveOneThousand() {
        BigDecimal atThreshold = calculator.calculateOrderTotal(
                List.of(lineItem("1000.00", 1)), "regular", null);
        BigDecimal aboveThreshold = calculator.calculateOrderTotal(
                List.of(lineItem("1000.01", 1)), "regular", null);

        assertThat(atThreshold).isEqualByComparingTo("1000.00");
        assertThat(aboveThreshold).isEqualByComparingTo("980.01");
    }

    private PricingCalculator.LineItem lineItem(String price, int quantity) {
        return new PricingCalculator.LineItem(new BigDecimal(price), quantity);
    }
}
