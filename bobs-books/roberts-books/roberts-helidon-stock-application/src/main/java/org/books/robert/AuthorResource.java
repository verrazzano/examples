// Copyright (c) 2020, Oracle and/or its affiliates.
// Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

package org.books.robert;

import java.util.List;
import java.util.stream.Collectors;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.metrics.annotation.Metered;
import org.eclipse.microprofile.opentracing.Traced;

@Path("authors")
@RequestScoped
@Traced
public class AuthorResource {

  @Inject io.opentracing.Tracer tracer;

  private final BookStore bookStore;
    private final List<String> authors;

    @Inject
    public AuthorResource(BookStore bookStore) {
        this.bookStore = bookStore;
        this.authors = bookStore.getBookCountByAuthor(6).stream()
                        .map(BookStore.BookCount::getAuthor)
                        .filter(author -> author.split(" ").length <= 3)
                        .sorted()
                        .collect(Collectors.toList());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Metered
	public Response getAuthors(@QueryParam("q") String query, @Context HttpHeaders httpHeaders) {
		tracer.activeSpan().log(String.format("query: %s", query));
		if (query != null) {
            return Response.ok(authors.stream()
                                       .filter(name -> name.toLowerCase().contains(query.toLowerCase()))
                                       .collect(Collectors.toList())).build();
        }
        return Response.ok(authors).build();
    }

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
	public Response getBookCountByAuthor(@QueryParam("min") int minCount, @Context HttpHeaders httpHeaders) {
 	  tracer.activeSpan().log(String.format("minCount: %d", minCount));
		List<?> counts = bookStore.getBookCountByAuthor(minCount);
		return Response.ok(counts).build();
    }
}
