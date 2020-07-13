// Copyright (c) 2020, Oracle and/or its affiliates.
// Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

package org.books.robert;

import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

@Path("v2/spans")
@RequestScoped
public class ZipkinProxy {
  private static final Logger logger = Logger.getLogger(ZipkinProxy.class.getName());

  private static String tracingEndPoint;

  static {
    String tracingPath = System.getProperty("TRACING_PATH", "api/v2/spans");
    String tracingHost = System.getProperty("TRACING_HOST", "zipkin.istio-system");
    String tracingPort = System.getProperty("TRACING_PORT", "9411");
    tracingEndPoint = String.format("http://%s:%s/%s", tracingHost, tracingPort, tracingPath);
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  // @Metered
  public Response trace(String trace, @Context HttpHeaders headers) {

    logger.info("ZipKinProxy: trace called");

    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
      final HttpPost httppost = new HttpPost(tracingEndPoint);
      for (String existingHeader : headers.getRequestHeaders().keySet()) {
        if (!existingHeader.toLowerCase().startsWith("content-length")) {
          logger.info("ZipKinProxy: Adding header ---> " + existingHeader);
          httppost.setHeader(existingHeader, headers.getHeaderString(existingHeader));
        }
      }
      httppost.setEntity(new StringEntity(trace));

      logger.info("ZipKinProxy: Sending trace");

      HttpResponse reponse = httpClient.execute(httppost);
      Response.ResponseBuilder responseToSend = Response.ok();

      logger.info("ZipKinProxy: Response is OK");

      for (Header header : reponse.getAllHeaders()) {
        responseToSend.header(header.getName(), header.getValue());
      }
      responseToSend.entity(reponse.getEntity().getContent());
      return responseToSend.build();

    } catch (Exception e) {
      logger.severe("Error posting span: " + e.getMessage());
      e.printStackTrace();
      return Response.serverError().build();
    }
  }
}
