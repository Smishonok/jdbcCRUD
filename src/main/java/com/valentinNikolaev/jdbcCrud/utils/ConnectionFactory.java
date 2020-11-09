package com.valentinNikolaev.jdbcCrud.utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;

public class ConnectionFactory {

    private static Properties properties;

    public static <T> T doTransaction(Function<Connection, T> transaction) {
        T result = null;
        try (Connection connection = getConnection()) {
            result = transaction.apply(connection);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static Connection getConnection() throws IOException, SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (properties == null) {
            initiateConnectionProperties();
        }

        String user = properties.getProperty("db.user");
        String password = properties.getProperty("db.password");
        String url = properties.getProperty("db.url");
        Connection connection = DriverManager.getConnection(url, user, password);
        connection.setAutoCommit(false);
        return connection;
    }

    private static void initiateConnectionProperties() throws IOException {
        if (properties == null) {
            properties = new Properties();
            InputStream inputStream = Objects.requireNonNull(ConnectionFactory.class
                                                                     .getClassLoader()
                                                                     .getResourceAsStream(
                                                                             "connection.properties"));
            properties.load(inputStream);
            inputStream.close();
        }
    }
}
