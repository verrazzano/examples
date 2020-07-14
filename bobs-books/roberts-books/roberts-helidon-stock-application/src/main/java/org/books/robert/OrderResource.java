// Copyright (c) 2020, Oracle and/or its affiliates.
// Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

package org.books.robert;

import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.metrics.annotation.Metered;
import org.eclipse.microprofile.opentracing.Traced;

@Path("orders")
@ApplicationScoped
@Traced
public class OrderResource {

  private final BookStore bookStore;
  private final AtomicLong orderId = new AtomicLong();
  private static final Logger logger = Logger.getLogger(OrderResource.class.getName());

  @Inject
  public OrderResource(BookStore bookStore) {
    this.bookStore = bookStore;
  }

  @Inject io.opentracing.Tracer tracer;

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Metered
  public Response submitOrder(String jsonOrder, @Context HttpHeaders httpHeaders) {

    tracer.activeSpan().log(String.format("Posting order: %s", jsonOrder));
    bookStore.submitOrder(orderId.incrementAndGet(), jsonOrder);
    // finishTracing(scope);
    return Response.noContent().build();
  }
}
