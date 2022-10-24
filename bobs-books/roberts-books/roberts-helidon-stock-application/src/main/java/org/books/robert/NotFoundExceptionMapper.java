// Copyright (c) 2022, Oracle and/or its affiliates.
// Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

package org.books.robert; 

import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.core.Response;
import java.net.URI;

@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<Exception> {
  public Response toResponse(Exception ex) {
	System.out.println("in exception mapper - exception type is: " + ex);
    return Response.temporaryRedirect(new URI("/")).build();
  }
}
