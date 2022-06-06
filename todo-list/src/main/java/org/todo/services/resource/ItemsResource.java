// Copyright (c) 2020, 2021, Oracle and/or its affiliates.
// Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

package org.todo.services.resource;

import org.todo.services.TodoService;
import org.todo.services.entity.Item;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * REST service for the To-Do list application using MySQL DB.
 * /items retrieves the full list of tasks
 * /items/init drops the table, creates the table, and loads the table with some starting tasks
 */
@Path("/items/")
@Produces(MediaType.APPLICATION_JSON)
public class ItemsResource {
  private static final TodoService TODO_SERVICE = TodoService.getDefault();
  public static DataSource datasource() throws NamingException {
    InitialContext ctx = new InitialContext();
    return (DataSource) ctx.lookup("jdbc/ToDoDB");
  }

  @GET
  public JsonArray itemsJson() {
    JsonArrayBuilder result = Json.createArrayBuilder();
    for (Item item : TODO_SERVICE.getAll()) {
      result.add(item.toJson());
    }
    return result.build();
  }

  @GET
  @Path("/drop/")
  @Produces(MediaType.TEXT_PLAIN)
  public Response dropTable() {
    try (Connection conn = datasource().getConnection()) {
      Statement stmt = conn.createStatement();

      String dropTable = "drop table ToDos;";
      System.out.println(dropTable);
      stmt.executeUpdate(dropTable);
    } catch (SQLException | NamingException ex) {
      // ok to fail, table may not exist yet.
      return Response.ok().entity(ex.getLocalizedMessage() + "\n").build();
    }
    return Response.ok().entity("ToDos table dropped.\n").build();
  }

  @GET
  @Path("/init/")
  @Produces(MediaType.TEXT_PLAIN)
  public Response initTable() {
    dropTable();
    try (Connection conn = datasource().getConnection()){
      Statement stmt = conn.createStatement();

      String createTable = TODO_SERVICE.getCreateTableStatement();

      System.out.println(createTable);
      stmt.executeUpdate(createTable);

      String[] tasks = {"Install Verrazzano", "Move ToDo List to the cloud", "Celebrate", "Clean off my desk"};
      for (String task : tasks) {
        TODO_SERVICE.insert(task);
      }

    } catch (SQLException | NamingException ex) {
      ex.printStackTrace();
      return Response.serverError().entity("ERROR: " + ex.getLocalizedMessage() + "\n").build();
    }
    return Response.ok().entity("ToDos table initialized.\n").build();
  }
}
