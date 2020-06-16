// Copyright (c) 2020, Oracle and/or its affiliates.

package org.books.bobby;

import io.opentracing.Span;
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

  @Inject
  io.opentracing.Tracer tracer;

  @GET
  @Metered
  @Produces(MediaType.APPLICATION_JSON)
  public Response getBooks(
      @QueryParam("start") int start, @QueryParam("end") int end, @Context HttpHeaders headers) {

    tracer.activeSpan().log(String.format("first-book: %d, last-book: %d", start, end));
    Collection<Book> books = bookStore.getRange(start, end);
    if (books != null) {;
      tracer.activeSpan().log(String.format("Returned no. of books:%d", books.size()));
    } else {;
      tracer.activeSpan().log(String.format("No books found"));
    }

    return Response.ok(books).header("Access-Control-Allow-Origin", "*").build();
  }

  @POST
  @Metered
  @Consumes(MediaType.APPLICATION_JSON)
  public Response postBook(Book book, @Context HttpHeaders headers) {
    tracer.activeSpan().log(String.format("bookId: %s", book.getBookId()));
    Optional<Book> optional = bookStore.find(book.getBookId());
    if (optional.isPresent()) {
      tracer
          .activeSpan()
          .log(String.format("Book with bookId %s already exists", optional.get().getBookId()));
      tracer.activeSpan().finish();
      return Response.status(Response.Status.CONFLICT).build();
    }
    bookStore.store(book);
    tracer.activeSpan().log(String.format("Stored Book with bookId %s", book.getBookId()));
    return Response.ok().header("Access-Control-Allow-Origin", "*").build();
  }

  @GET
  @Path("{id}")
  @Metered
  @Produces(MediaType.APPLICATION_JSON)
  public Response getBook(@PathParam("id") String id, @Context HttpHeaders headers) {
    tracer.activeSpan().log(String.format("id: %s", id));
    Optional<Book> optional = bookStore.find(id);
    tracer
        .activeSpan()
        .log(
            String.format(
                "Book with bookId %s %s", id, optional.isPresent() ? "found" : "not found"));
    return optional.isPresent()
        ? Response.ok(optional.get()).build()
        : Response.status(Response.Status.NOT_FOUND)
            .header("Access-Control-Allow-Origin", "*")
            .build();
  }

  @PUT
  @Path("{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Metered
  public Response putBook(Book book, @Context HttpHeaders headers) {
    tracer.activeSpan().log(String.format("bookId: %s", book.getBookId()));
    Optional<Book> optional = bookStore.find(book.getBookId());
    if (!optional.isPresent()) {
      tracer
          .activeSpan()
          .log(String.format("Book with bookId %s %s", book.getBookId(), "not found"));
      tracer.activeSpan().finish();
      return Response.status(Response.Status.NOT_FOUND)
          .header("Access-Control-Allow-Origin", "*")
          .build();
    }
    bookStore.store(book);
    tracer.activeSpan().log(String.format("Updated Book with bookId %s", book.getBookId()));
    return Response.ok().header("Access-Control-Allow-Origin", "*").build();
  }

  @DELETE
  @Path("{id}")
  @Metered
  public Response deleteBook(@PathParam("id") String id, @Context HttpHeaders headers) {
    tracer.activeSpan().log(String.format("id: %s", id));
    Optional<Book> optional = bookStore.find(id);
    if (!optional.isPresent()) {
      tracer.activeSpan().log(String.format("Book with bookId %s %s", id, "not found"));
      tracer.activeSpan().finish();
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    bookStore.remove(id);
    tracer.activeSpan().log(String.format("Removed Book with bookId %s", id));
    return Response.ok().header("Access-Control-Allow-Origin", "*").build();
  }
}
