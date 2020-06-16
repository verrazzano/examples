// Copyright (c) 2020, Oracle Corporation and/or its affiliates.

package org.books;

import org.books.model.Book;
import org.books.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.*;

import javax.annotation.Resource;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import static org.books.utils.TracingUtils.*;

import java.sql.*;

import io.opentracing.Scope;
import io.opentracing.Span;


// Specifies the path to the RESTful service
@Path("/order")
public class OrderResource {

    private static final JsonBuilderFactory bf = Json.createBuilderFactory(null);
	private static Logger logger = LoggerFactory.getLogger(OrderResource.class);

//    @Resource(lookup = "java:jdbc/books")
//    DataSource booksDS;

    @Context
    HttpHeaders httpHeaders;

    // get orders from database
    @GET
    @Produces("application/json")
    public JsonArray getOrders() {
		Scope tracingScope = null;
        try {
			Span tracingSpan = buildSpan("orderResource.getOrders", httpHeaders);
            InitialContext ctx = new InitialContext();
            DataSource booksDS = (DataSource) ctx.lookup("jdbc/books");
            Connection connection = booksDS.getConnection();
			tracingScope = startTracing(tracingSpan, connection);
            Statement statement = connection.createStatement();
            ResultSet resultSet =
                    statement.executeQuery("select id, order_date, name, street, city, state from orders");
            JsonArrayBuilder jab = bf.createArrayBuilder();
            while (resultSet.next()) {
                Statement innerStatement = connection.createStatement();
                ResultSet innerResultSet =
                        innerStatement.executeQuery("select book_id, title from order_books " +
                                "where order_id = " + resultSet.getInt("id"));
                JsonArrayBuilder bab = bf.createArrayBuilder();
                while (innerResultSet.next()) {
                    bab.add(bf.createObjectBuilder()
                            .add("book_id", innerResultSet.getInt("book_id"))
                            .add("title", innerResultSet.getString("title"))
                            .build());
                }
                JsonObjectBuilder job = bf.createObjectBuilder()
                        .add("id", resultSet.getInt("id"))
                        .add("order_date", resultSet.getDate("order_date").toString())
                        .add("customer", bf.createObjectBuilder()
                                .add("name", resultSet.getString("name"))
                                .add("street", resultSet.getString("street"))
                                .add("city", resultSet.getString("city"))
                                .add("state", resultSet.getString("state")).build())
                        .add("books", bab);
                jab.add(job.build());
                innerResultSet.close();
            }
            resultSet.close();
            connection.close();
            return (jab.build());

        } catch (Exception e) {
			logger.error("Error accessing database", e);
            return bf.createArrayBuilder()
                    .add(bf.createObjectBuilder()
                            .add("database", "error"))
                    .build();
		} finally {
			if (tracingScope != null) {
				finishTracing(tracingScope);
			}
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void createOrder(Order order) {
		Scope tracingScope = null;
        try {
			Span tracingSpan = buildSpan("orderResource.createOrder", httpHeaders);
			logger.info("[order manager] order=" + order.toString());
            InitialContext ctx = new InitialContext();
            DataSource booksDS = (DataSource) ctx.lookup("jdbc/books");
            Connection connection = booksDS.getConnection();
			tracingScope = startTracing(tracingSpan, connection);
            PreparedStatement statement = connection.prepareStatement(
                    "insert into orders (order_date, name, street, city, state) " +
							"values (sysdate, ?, ?, ?, ?)");
            statement.setString(1, order.getCustomer().getName());
            statement.setString(2, order.getCustomer().getStreet());
            statement.setString(3, order.getCustomer().getCity());
            statement.setString(4, order.getCustomer().getState());
            statement.execute();
            statement.close();

            Statement statement2 = connection.createStatement();
            ResultSet rs2 = statement2.executeQuery("select max(id) as order_id from orders");
            int orderId = -1;
            while(rs2.next()) {
                orderId = rs2.getInt("order_id");
            }
            rs2.close();
            statement2.close();

            for (Book book : order.getBooks()) {
                PreparedStatement innerStatement = connection.prepareStatement(
                        "insert into order_books (order_id, book_id, title) " +
                                "values (?, ?, ?)");
                innerStatement.setInt(1, orderId);
                innerStatement.setInt(2, book.getBookId());
                innerStatement.setString(3, book.getTitle());
                innerStatement.execute();
                innerStatement.close();
            }

            connection.close();

        } catch (Exception e) {
			logger.error("Error accessing database", e);
		} finally {
			if (tracingScope != null) {
				finishTracing(tracingScope);
			}
		}
    }

	private Scope startTracing(Span tracingSpan, Connection connection) throws SQLException {
		tracingSpan.setTag("TimeSpentInDBOperationFor_" + connection, "TODO");
		tracingSpan.setBaggageItem("DatabaseProductName", connection.getMetaData().getDatabaseProductName());
		return activateSpan(tracingSpan);
    }
}
