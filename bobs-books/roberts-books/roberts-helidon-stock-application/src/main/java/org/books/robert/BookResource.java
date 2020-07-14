// Copyright (c) 2020, Oracle and/or its affiliates.
// Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

package org.books.robert;

import java.util.Collection;
import java.util.Optional;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.metrics.annotation.Metered;
import org.eclipse.microprofile.opentracing.Traced;

@Path("/books")
@RequestScoped
@Traced
public class BookResource {

  private final BookStore bookStore;

  @Inject
  public BookResource(BookStore bookStore) {
    this.bookStore = bookStore;
  }

  @Inject io.opentracing.Tracer tracer;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Metered
  public Response getBooks(
      @QueryParam("count") int count,
      @QueryParam("author") String author,
      @Context HttpHeaders headers) {
    tracer.activeSpan().log(String.format("count: %d, end: %s", count, author));
    if (count == 0) {
      count = 10;
    }
    Collection<Book> books =
        author == null ? bookStore.getTopRatedBooks(count) : bookStore.getBooksByAuthor(author);
    return Response.ok(books).build();
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Metered
  public Response addBook(Book book, @Context HttpHeaders headers) {
    tracer.activeSpan().log(String.format("bookId: %s", book.getBookId()));
    Optional<Book> optional = bookStore.find(book.getBookId());
    if (optional.isPresent()) {
      return Response.status(Response.Status.CONFLICT).build();
    }
    bookStore.store(book);
    return Response.status(Response.Status.CREATED).build();
  }

  @GET
  @Path("{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Metered
  public Response getBook(@PathParam("id") String id, @Context HttpHeaders headers) {
    tracer.activeSpan().log(String.format("id: %s", id));
    Optional<Book> optional = bookStore.find(id);
    return optional.isPresent()
        ? Response.ok(optional.get()).build()
        : Response.status(Response.Status.NOT_FOUND).build();
  }

  @PUT
  @Path("{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Metered
  public Response updateBook(Book book, @Context HttpHeaders headers) {
    tracer.activeSpan().log(String.format("bookId: %s", book.getBookId()));
    Optional<Book> optional = bookStore.find(book.getBookId());
    if (!optional.isPresent()) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    bookStore.store(book);
    return Response.status(Response.Status.ACCEPTED).build();
  }

  @DELETE
  @Path("{id}")
  @Metered
  public Response deleteBook(@PathParam("id") String id, @Context HttpHeaders headers) {
    tracer.activeSpan().log(String.format("id: %s", id));
    Optional<Book> optional = bookStore.find(id);
    if (!optional.isPresent()) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    bookStore.remove(id);
    return Response.status(Response.Status.NO_CONTENT).build();
  }
}
