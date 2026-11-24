package com.rbdip.bookstore.customer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.regex.Pattern;

@Entity
@Table(name = "customers")
public class Customer {

    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column
    private String address;

    @Column
    private String phone;

    protected Customer() {
        // for JPA
    }

    public Customer(String fullName, String address, String phone) {
        String normalizedName = WHITESPACE.matcher(fullName.trim()).replaceAll(" ");
        int separator = normalizedName.indexOf(' ');
        if (separator < 0) {
            this.firstName = normalizedName;
            this.lastName = "";
        } else {
            this.firstName = normalizedName.substring(0, separator);
            this.lastName = normalizedName.substring(separator + 1);
        }
        this.address = address;
        this.phone = phone;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getFullName() {
        return lastName.isEmpty() ? firstName : firstName + " " + lastName;
    }
}
