<%-- Copyright (c) 2020, Oracle Corporation and/or its affiliates. --%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="javax.naming.InitialContext" %>
<%@ page import="javax.sql.DataSource" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.Statement" %>

<html>
<head>
    <title>Order Manager</title>
</head>
<body>
<h1>Bob's Order Manager</h1>
<h2>Super simple list of orders</h2>
<table border="1">
  <tr>
    <th>Order ID</th>
    <th>Order Date</th>
    <th>Name</td>
    <th>Street</th>
    <th>City</th>
    <th>State</th>
    <th>Books</th>
   </tr>
<%
    InitialContext ctx = new InitialContext();
    DataSource booksDS = (DataSource) ctx.lookup("jdbc/books");
    Connection connection = booksDS.getConnection();
    Statement statement = connection.createStatement();
    ResultSet resultSet =
       statement.executeQuery("select id, order_date, name, street, city, state from orders");
    while (resultSet.next()) {
%>
   <tr>
     <td><%= resultSet.getInt("id") %></td>
     <td><%= resultSet.getDate("order_date").toString() %></td>
     <td><%= resultSet.getString("name") %></td>
     <td><%= resultSet.getString("street") %></td>
     <td><%= resultSet.getString("city") %></td>
     <td><%= resultSet.getString("state") %></td>
     <td><a href="./books?order_id=<%= resultSet.getInt("id") %>">View Books</a></td>
   </tr>
<%
   }
%>
  </table>

<%
    resultSet.close();
    connection.close();
%>
</body>
</html>