// Copyright (c) 2020, Oracle and/or its affiliates.
// Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

package org.books;


import org.books.bobby.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.opentracing.Scope;
import io.opentracing.Span;


import javax.enterprise.context.SessionScoped;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import static org.books.utils.TracingUtils.activateSpan;
import static org.books.utils.TracingUtils.buildSpan;
import static org.books.utils.TracingUtils.finishTrace;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@SessionScoped
public class CartBean implements Serializable {

	private static Logger logger = LoggerFactory.getLogger(CartBean.class);
    private List<Book> books = new ArrayList<>();
    private String name;
    private String street;
    private String city;
    private String state;

	@Inject
	private HttpServletRequest servletRequest;

    public void addBook() {
		Span tracingSpan = buildSpan("CartBean.addBook", servletRequest);


        FacesContext facesContext = FacesContext.getCurrentInstance();
        BookBean bookBean
                = (BookBean) facesContext.getApplication()
                .getVariableResolver().resolveVariable(facesContext, "bookBean");

		Scope scope = tracerPreprocessing(bookBean, tracingSpan);
        Book book = new Book();
        book.setBookId(bookBean.getBookId());
        book.setTitle(bookBean.getTitle());
        book.setImageUrl(bookBean.getImageUrl());
        book.setAuthors(bookBean.getAuthors());

        this.books.add(book);


		logger.info("[front end] books in cart=" + books.toString());
		tracerPostprocessing(scope);
    }

	private Scope tracerPreprocessing(BookBean bookBean, Span tracingSpan) {
		if (bookBean != null) {
			tracingSpan.setTag("bookTitle[" + "[", bookBean.getTitle());
		}
		return activateSpan(tracingSpan);
	}

	private void tracerPostprocessing(Scope scope) {
		finishTrace(scope);
	}

	public void setBooks(List<Book> books) {
        this.books = books;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<Book> getBooks() {
        return books;
    }

    @Override
    public String toString() {
        return "CartBean{" +
                "books=" + books +
                ", name='" + name + '\'' +
                ", street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}
