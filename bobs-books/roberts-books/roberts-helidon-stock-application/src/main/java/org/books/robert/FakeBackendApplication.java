// Copyright (c) 2020 Oracle and/or its affiliates.

package org.books.robert;

import io.helidon.common.CollectionsHelper;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@ApplicationScoped
@ApplicationPath("/bobs-bookstore-order-manager")
public class FakeBackendApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        return CollectionsHelper.setOf(OrderManager.class);
    }

    @Path("order")
    public static class OrderManager {
        @POST
        @Consumes(MediaType.APPLICATION_JSON)
        public Response postOrder(String jsonOrder) {
            System.out.println("FAKE BACKEND RECEIVED ORDER: " + jsonOrder);
            return Response.noContent().build();
        }
    }
}
