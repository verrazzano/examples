// Copyright (c) 2020, Oracle Corporation and/or its affiliates.

package org.books.bobby;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class BookLoader extends AbstractLoader<String, Book> {
    @Override
    public Map<String, Book> loadData(InputStream in) throws IOException {
        Map<String, Book> books = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {

            String line;
            String cvsSplitBy = ",";
            boolean fFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (!fFirstLine) {
                    // use comma as separator
                    String[] bookData = line.split(cvsSplitBy);
                    Book book = new Book(bookData);

                    books.put(book.getBookId(), book);
                }
                fFirstLine = false;
            }
        }

        return books;
    }
}
