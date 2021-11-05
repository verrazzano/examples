package org.todo.services;

import org.todo.services.resource.ItemsResource;

import javax.naming.NamingException;
import java.sql.*;

public class MySQLService extends TodoService {
    // create app table for MySQL
    private static final String MYSQL_CREATE_TABLE = "create table ToDos (" +
            "taskId INT NOT NULL AUTO_INCREMENT, " +
            "task VARCHAR(200) NOT NULL, " +
            "completed BOOLEAN NOT NULL DEFAULT 0," +
            "constraint todo_pk PRIMARY KEY (taskId));";

    @Override
    public String getCreateTableStatement() {
        return MYSQL_CREATE_TABLE;
    }

    @Override
    public boolean getComplete(ResultSet resultSet) throws SQLException {
        return resultSet.getBoolean("completed");
    }

    @Override
    public void update(int id, String status) {
        try (Connection conn = ItemsResource.datasource().getConnection()) {
            PreparedStatement statement = conn.prepareStatement(updateSql);
            System.out.println(updateSql);
            statement.setBoolean(1, Boolean.parseBoolean(status));
            statement.setInt(2, id);
            statement.executeUpdate();
        } catch (SQLException | NamingException ex) {
            throw new RuntimeException(ex);
        }
    }
}
