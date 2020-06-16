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
<p><a href="./orders">Back to orders list</a></p>
<p>
<h3>Books for order <%= request.getParameter("order_id") %></h3>
<table border="1">
  <tr>
    <th>Book ID</th>
    <th>Title</th>
   </tr>
<%
    InitialContext ctx = new InitialContext();
    DataSource booksDS = (DataSource) ctx.lookup("jdbc/books");
    Connection connection = booksDS.getConnection();
    Statement statement = connection.createStatement();
    ResultSet resultSet =
       statement.executeQuery("select book_id, title from order_books where order_id = "
       + request.getParameter("order_id"));
    while (resultSet.next()) {
%>
   <tr>
     <td><%= resultSet.getInt("book_id") %></td>
     <td><%= resultSet.getString("title") %></td>
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