// Copyright (c) 2020, Oracle Corporation and/or its affiliates.

package org.books.bobby;

import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofSerializer;
import com.tangosol.io.pof.PofWriter;

import java.io.IOException;

public class BookSerializer implements PofSerializer<Book> {

    @Override
    public void serialize(PofWriter writer, Book book) throws IOException {
        writer.writeString(0, book.getBookId());
        writer.writeString(1, book.getGoodreadsBookId());
        writer.writeString(2, book.getBestBookId());
        writer.writeString(3, book.getWorkId());
        writer.writeString(4, book.getBooksCount());
        writer.writeString(5, book.getIsbn());
        writer.writeString(6, book.getIsbn13());
        writer.writeString(7, book.getAuthors());
        writer.writeString(8, book.getOriginalPublicationYear());
        writer.writeString(9, book.getOriginalTitle());
        writer.writeString(10, book.getTitle());
        writer.writeString(11, book.getLanguageCode());
        writer.writeString(12, book.getAverageRating());
        writer.writeString(13, book.getRatingsCount());
        writer.writeString(14, book.getWorkRatingsCount());
        writer.writeString(15, book.getWorkTextReviewsCount());
        writer.writeString(16, book.getRatings1());
        writer.writeString(17, book.getRatings2());
        writer.writeString(18, book.getRatings3());
        writer.writeString(19, book.getRatings4());
        writer.writeString(20, book.getRatings5());
        writer.writeString(21, book.getImageUrl());
        writer.writeString(22, book.getSmallImageUrl());
        writer.writeRemainder(null);
    }

    @Override
    public Book deserialize(PofReader reader) throws IOException {
        Book book = new Book();

        book.setBookId(reader.readString(0));
        book.setGoodreadsBookId(reader.readString(1));
        book.setBestBookId(reader.readString(2));
        book.setWorkId(reader.readString(3));
        book.setBooksCount(reader.readString(4));
        book.setIsbn(reader.readString(5));
        book.setIsbn13(reader.readString(6));
        book.setAuthors(reader.readString(7));
        book.setOriginalPublicationYear(reader.readString(8));
        book.setOriginalTitle(reader.readString(9));
        book.setTitle(reader.readString(10));
        book.setLanguageCode(reader.readString(11));
        book.setAverageRating(reader.readString(12));
        book.setRatingsCount(reader.readString(13));
        book.setWorkRatingsCount(reader.readString(14));
        book.setWorkTextReviewsCount(reader.readString(15));
        book.setRatings1(reader.readString(16));
        book.setRatings2(reader.readString(17));
        book.setRatings3(reader.readString(18));
        book.setRatings4(reader.readString(19));
        book.setRatings5(reader.readString(20));
        book.setImageUrl(reader.readString(21));
        book.setSmallImageUrl(reader.readString(22));
        reader.readRemainder();
        return book;
    }
}
