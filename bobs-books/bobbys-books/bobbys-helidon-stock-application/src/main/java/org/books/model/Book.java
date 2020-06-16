// Copyright (c) 2020, Oracle and/or its affiliates.

package org.books.model;

import java.io.Serializable;

public class Book implements Serializable {
    private int bookId;
    private String title;

    public Book() {};

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Book{" +
                "bookId=" + bookId +
                ", title='" + title + '\'' +
                '}';
    }
}