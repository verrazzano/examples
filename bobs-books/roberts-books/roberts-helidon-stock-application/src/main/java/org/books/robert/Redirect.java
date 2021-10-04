// Copyright (c) 2021, Oracle and/or its affiliates.
// Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

package org.books.robert;

import org.eclipse.microprofile.opentracing.Traced;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.core.Application;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

@ApplicationScoped
@ApplicationPath("/")
public class Redirect extends Application {
    private static final URI URI;

    static {
        try {
            URI = new URI("/");
        } catch (URISyntaxException e) {
            throw new RuntimeException("Malformed redirect URI", e);
        }
    }

    public static URI getURI() {
        return URI;
    }

    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(
                BooksRedirect.class,
                CartRedirect.class
        );
    }

    @Path("/books")
    @RequestScoped
    @Traced
    public static class BooksRedirect {
        @GET
        public Response books() {
            return Response.temporaryRedirect(Redirect.getURI()).build();
        }
    }

    @Path("/cart")
    @RequestScoped
    @Traced
    public static class CartRedirect {
        @GET
        public Response cart() {
            return Response.temporaryRedirect(Redirect.getURI()).build();
        }
    }
}
