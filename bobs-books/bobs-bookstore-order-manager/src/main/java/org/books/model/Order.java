// Copyright (c) 2020, Oracle Corporation and/or its affiliates.

package org.books.model;

import java.io.Serializable;
import java.util.List;

public class Order implements Serializable {
    private Customer customer;
    private List<Book> books;

    public Order() {}

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    @Override
    public String toString() {
        return "Order{" +
                "customer=" + customer +
                ", books=" + books +
                '}';
    }

}
