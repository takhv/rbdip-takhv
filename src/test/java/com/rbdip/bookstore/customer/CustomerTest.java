package com.rbdip.bookstore.customer;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CustomerTest {

    @Test
    void splitsNameAndRetainsRemainingPartsAsLastName() {
        Customer customer = new Customer("  Ivan   Sergeevich Petrov  ", "Moscow", "+7000");

        assertThat(customer.getFirstName()).isEqualTo("Ivan");
        assertThat(customer.getLastName()).isEqualTo("Sergeevich Petrov");
        assertThat(customer.getFullName()).isEqualTo("Ivan Sergeevich Petrov");
        assertThat(customer.getAddress()).isEqualTo("Moscow");
        assertThat(customer.getPhone()).isEqualTo("+7000");
        assertThat(customer.getId()).isNull();
    }

    @Test
    void supportsSinglePartName() {
        Customer customer = new Customer("Madonna", null, null);

        assertThat(customer.getFirstName()).isEqualTo("Madonna");
        assertThat(customer.getLastName()).isEmpty();
        assertThat(customer.getFullName()).isEqualTo("Madonna");
    }
}
