// Copyright (c) 2020, Oracle and/or its affiliates.
// Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

package org.books.bobby;

public class Book {

    private String bookId;
    private String goodreadsBookId;
    private String bestBookId;
    private String workId;
    private String booksCount;
    private String isbn;
    private String isbn13;
    private String authors;
    private String originalPublicationYear;
    private String originalTitle;
    private String title;
    private String languageCode;
    private String averageRating;
    private String ratingsCount;
    private String workRatingsCount;
    private String workTextReviewsCount;
    private String ratings1;
    private String ratings2;
    private String ratings3;
    private String ratings4;
    private String ratings5;
    private String imageUrl;
    private String smallImageUrl;

    public Book() {
    }

    public Book(String[] data) {
        this(data[0],
             data[1],
             data[2],
             data[3],
             data[4],
             data[5],
             data[6],
             data[7],
             data[8],
             data[9],
             data[10],
             data[11],
             data[12],
             data[13],
             data[14],
             data[15],
             data[16],
             data[17],
             data[18],
             data[19],
             data[20],
             data[21],
             data[22]);
    }

    public Book(String bookId,
                String goodreadsBookId,
                String bestBookId,
                String workId,
                String booksCount,
                String isbn,
                String isbn13,
                String authors,
                String originalPublicationYear,
                String originalTitle,
                String title,
                String languageCode,
                String averageRating,
                String ratingsCount,
                String workRatingsCount,
                String workTextReviewsCount,
                String ratings1,
                String ratings2,
                String ratings3,
                String ratings4,
                String ratings5,
                String imageUrl,
                String smallImageUrl) {
        this.bookId = bookId;
        this.goodreadsBookId = goodreadsBookId;
        this.bestBookId = bestBookId;
        this.workId = workId;
        this.booksCount = booksCount;
        this.isbn = isbn;
        this.isbn13 = isbn13;
        this.authors = authors;
        this.originalPublicationYear = originalPublicationYear;
        this.originalTitle = originalTitle;
        this.title = title;
        this.languageCode = languageCode;
        this.averageRating = averageRating;
        this.ratingsCount = ratingsCount;
        this.workRatingsCount = workRatingsCount;
        this.workTextReviewsCount = workTextReviewsCount;
        this.ratings1 = ratings1;
        this.ratings2 = ratings2;
        this.ratings3 = ratings3;
        this.ratings4 = ratings4;
        this.ratings5 = ratings5;
        this.imageUrl = imageUrl;
        this.smallImageUrl = smallImageUrl;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getGoodreadsBookId() {
        return goodreadsBookId;
    }

    public void setGoodreadsBookId(String goodreadsBookId) {
        this.goodreadsBookId = goodreadsBookId;
    }

    public String getBestBookId() {
        return bestBookId;
    }

    public void setBestBookId(String bestBookId) {
        this.bestBookId = bestBookId;
    }

    public String getWorkId() {
        return workId;
    }

    public void setWorkId(String workId) {
        this.workId = workId;
    }

    public String getBooksCount() {
        return booksCount;
    }

    public void setBooksCount(String booksCount) {
        this.booksCount = booksCount;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getOriginalPublicationYear() {
        return originalPublicationYear;
    }

    public void setOriginalPublicationYear(String originalPublicationYear) {
        this.originalPublicationYear = originalPublicationYear;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(String averageRating) {
        this.averageRating = averageRating;
    }

    public String getRatingsCount() {
        return ratingsCount;
    }

    public void setRatingsCount(String ratingsCount) {
        this.ratingsCount = ratingsCount;
    }

    public String getWorkRatingsCount() {
        return workRatingsCount;
    }

    public void setWorkRatingsCount(String workRatingsCount) {
        this.workRatingsCount = workRatingsCount;
    }

    public String getWorkTextReviewsCount() {
        return workTextReviewsCount;
    }

    public void setWorkTextReviewsCount(String workTextReviewsCount) {
        this.workTextReviewsCount = workTextReviewsCount;
    }

    public String getRatings1() {
        return ratings1;
    }

    public void setRatings1(String ratings1) {
        this.ratings1 = ratings1;
    }

    public String getRatings2() {
        return ratings2;
    }

    public void setRatings2(String ratings2) {
        this.ratings2 = ratings2;
    }

    public String getRatings3() {
        return ratings3;
    }

    public void setRatings3(String ratings3) {
        this.ratings3 = ratings3;
    }

    public String getRatings4() {
        return ratings4;
    }

    public void setRatings4(String ratings4) {
        this.ratings4 = ratings4;
    }

    public String getRatings5() {
        return ratings5;
    }

    public void setRatings5(String ratings5) {
        this.ratings5 = ratings5;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSmallImageUrl() {
        return smallImageUrl;
    }

    public void setSmallImageUrl(String smallImageUrl) {
        this.smallImageUrl = smallImageUrl;
    }

    @Override
    public String toString() {
        return "Book{" +
                "bookId='" + bookId + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
