// Copyright (c) 2020, 2021 Oracle and/or its affiliates.
// Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

package org.todo.services.resource;

import org.todo.services.TodoService;
import org.todo.services.entity.Item;

import javax.json.JsonObject;
import javax.ws.rs.*;

@Path("/item")
public class ItemResource {
  private static final TodoService TODO_SERVICE = TodoService.getDefault();

  @GET
  @Path("/{id}")
  @Produces("application/json")
  public JsonObject item(@PathParam("id") int id) {
    Item result = TODO_SERVICE.getById(id);
    return result == null ? null : result.toJson();
  }

  @DELETE
  @Path("/{id}")
  public void delete(@PathParam("id") int id) {
    TODO_SERVICE.delete(id);
  }

  @PUT
  @Path("/{taskDescription}")
  public void addNewItem(@PathParam("taskDescription") String description) {
    TODO_SERVICE.insert(description);
  }

  @PUT
  @Path("/{id}/{status}")
  public void updateStatus(@PathParam("id") int id, @PathParam("status") String status) {
    TODO_SERVICE.update(id, status);
  }
}
