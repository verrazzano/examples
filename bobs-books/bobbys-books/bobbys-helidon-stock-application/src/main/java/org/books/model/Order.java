// Copyright (c) 2020, Oracle and/or its affiliates.
// Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

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

    public String toJsonString() {
        StringBuffer value = new StringBuffer();
        value.append("{ \"customer\": { \"name\": \"");
        value.append(customer.getName());
        value.append("\", \"street\": \"");
        value.append(customer.getStreet());
        value.append("\", \"city\": \"");
        value.append(customer.getCity());
        value.append("\", \"state\": \"");
        value.append(customer.getState());
        value.append("\" }, \"books\": [ ");

        boolean first = true;
        for (Book book : books) {
            if (!first) {
                value.append(",");
            }
            value.append("{ \"bookId\": \"");
            value.append(book.getBookId());
            value.append("\", \"title\": \"");
            value.append(book.getTitle());
            value.append("\" }");
            first = false;
        }
        value.append(" ] }");

        return value.toString();
    }

}
