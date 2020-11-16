package com.valentinNikolaev.jdbcCrud.utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.function.Function;

public class TestConnection {
    public ConnectionFactory getConnectionFactory() {
        Properties properties = new Properties();
        properties.setProperty("db.user", "root");
        properties.setProperty("db.password", "niscoraguaManagua86");
        properties.setProperty("db.url",
                               "jdbc:mysql://localhost:3306/jdbcCrudTest?serverTimezone=UTC");
        return new ConnectionFactory(properties);
    }

    public <T> T doTransaction(Function<Connection, T> transaction) {
        return getConnectionFactory().doTransaction(transaction);
    }

    public void cleanDataBase() {
        Function<Connection, Void> transaction = connection->{
            try {
                Statement statement = connection.createStatement();
                statement.executeUpdate("set foreign_key_checks = 0");
                statement.executeUpdate("truncate users");
                statement.executeUpdate("truncate posts");
                statement.executeUpdate("truncate regions");
                statement.executeUpdate("set foreign_key_checks =1");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return null;
        };
        doTransaction(transaction);
    }
}
