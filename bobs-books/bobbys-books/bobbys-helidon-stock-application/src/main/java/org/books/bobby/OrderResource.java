// Copyright (c) 2020, Oracle and/or its affiliates.
// Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

package org.books.bobby;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.books.model.Order;
import org.eclipse.microprofile.metrics.annotation.Metered;
import org.eclipse.microprofile.opentracing.Traced;
import org.glassfish.jersey.server.Uri;

@Path("/order")
@RequestScoped
public class OrderResource {

  @Inject
  io.opentracing.Tracer tracer;

  private static final Random random = new Random();
  private static final Logger logger = Logger.getLogger(OrderResource.class.getName());

  @Uri("http://{backend-host}:{backend-port}/bobs-bookstore-order-manager/order")
  private WebTarget target;

  @POST
  @Metered
  @Consumes(MediaType.APPLICATION_JSON)
  @Traced(operationName = "order-bobs.bookstore")
  public Response order(Order order, @Context HttpHeaders httpHeaders) {

    logger.info("OrderResource: order called");

    String hostname = System.getenv("BACKEND_HOSTNAME");
    String port = System.getenv("BACKEND_PORT");

    logger.info("OrderResource: host = " + hostname);
    logger.info("OrderResource: port = " + port);

    Map<String, Object> paramMap = new HashMap<>();
    paramMap.put("backend-host", hostname);
    paramMap.put("backend-port", port);

    logger.info("OrderResource: Resolved WebTarget " +
        target.resolveTemplates(paramMap).toString());

    /*
     * NOTE: Tracing is enabled because the pom.xml includes a helidon tracing artifact (e.g. helidon-tracing-zipkin)
     * This will automatically enable tracing for JAX-RS calls.  By using the WebTarget to call bob's back-end,
     * Helidon will automatically create a new child span in the same trace so that the tracing done in bob is
     * part of the overall trace that in progress for this call (as child spans).
     */
    tracer.activeSpan().setTag(TraceUtils.TAG_CONNECTION, TraceUtils.TAG_BOB);
    tracer.activeSpan().log("Calling bobs-bookstore at " + target.resolveTemplates(paramMap).toString());

    Response resp = target.resolveTemplates(paramMap)
                          .request()
                          .post(Entity.entity(order.toJsonString(), MediaType.APPLICATION_JSON_TYPE));
    
    logger.info("OrderResource: Response from bob backend is " + resp.toString());
    tracer.activeSpan().log("OrderResource: Response from bob backend is " + resp.toString());

    if (resp.getStatus() == 204) {
      resp =  Response.ok().header("Access-Control-Allow-Origin", "*").build();
      logger.info("OrderResource: Replaced response with " + resp.toString());
    }

    return resp;
  }
}
