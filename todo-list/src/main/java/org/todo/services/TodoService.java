// Copyright (c) 2021 Oracle and/or its affiliates.
// Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

package org.todo.services;

import org.todo.services.entity.Item;
import org.todo.services.resource.ItemsResource;

import javax.naming.NamingException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Handle database specific logic for ToDo app
public abstract class TodoService {
    protected final static String selectSql = "select task, completed from ToDos where taskId = ?";
    protected final static String deleteSql = "delete from ToDos where taskId = ?";
    protected final static String insertSql = "insert into ToDos(task) values(?)";
    protected final static String updateSql = "update ToDos set completed = ? where taskId = ?";
    protected final static String getAllSql = "select taskId, task, completed from ToDos";

    private static TodoService TODO_SERVICE = null;

    public static TodoService getDefault() {
        if (TODO_SERVICE == null) {
            String queryServiceClass = System.getenv("QUERY_SERVICE_CLASS");
            if (queryServiceClass == null) {
                TODO_SERVICE = new MySQLService();
            } else {
                try {
                    TODO_SERVICE = (TodoService) Class.forName(queryServiceClass).getConstructor().newInstance();
                } catch (Exception e) {
                    TODO_SERVICE = new MySQLService();
                }
            }

            System.out.println("Created TODO Service: " + TODO_SERVICE.getClass().getName());
        }

        return TODO_SERVICE;
    }

    public abstract String getCreateTableStatement();

    public abstract void update(int id, String status);

    abstract boolean getComplete(ResultSet resultSet) throws SQLException;

    public List<Item> parseResults(ResultSet resultSet) throws SQLException {
        List<Item> result = new ArrayList<>();
        while (resultSet.next()) {
            int id = resultSet.getInt("taskId");
            String task = resultSet.getString("task");
            boolean complete = getComplete(resultSet);
            result.add(new Item(id).desc(task).done(complete));
        }

        return result;
    }

    public Item parseResult(ResultSet resultSet, int id) throws SQLException {
        Item item = null;
        if (resultSet.next()) {
            String task = resultSet.getString("task");
            boolean complete = getComplete(resultSet);
            item = new Item(id).desc(task).done(complete);
        }

        return item;
    }

    public void insert(String description) {
        try (Connection conn = ItemsResource.datasource().getConnection()) {
            PreparedStatement statement = conn.prepareStatement(insertSql);
            statement.setString(1, description);
            statement.executeUpdate();
        } catch (SQLException | NamingException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void delete(int id) {
        try (Connection conn = ItemsResource.datasource().getConnection()) {
            PreparedStatement statement = conn.prepareStatement(deleteSql);
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException | NamingException ex) {
            throw new RuntimeException(ex);
        }
    }

    public Item getById(int id) {
        try (Connection conn = ItemsResource.datasource().getConnection()) {
            PreparedStatement statement = conn.prepareStatement(selectSql);
            statement.setInt(1, id);
            ResultSet results = statement.executeQuery();
            return parseResult(results, id);
        } catch (SQLException | NamingException ex) {
            throw new RuntimeException(ex);
        }
    }

    public List<Item> getAll() {
        try (Connection conn = ItemsResource.datasource().getConnection()){
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(getAllSql);
            return this.parseResults(resultSet);
        } catch (SQLException | NamingException ex) {
            throw new RuntimeException(ex);
        }
    }
}
