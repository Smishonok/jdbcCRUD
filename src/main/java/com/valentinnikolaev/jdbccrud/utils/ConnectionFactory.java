package com.valentinnikolaev.jdbccrud.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;

public class ConnectionFactory {

    private static final Logger log = LogManager.getLogger();

    private static volatile Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            synchronized (ConnectionFactory.class) {
                if (connection == null) {
                    initiateConnection();
                }
            }
        }
        return connection;
    }

    private static void initiateConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Properties properties = getProperties();
            String user = properties.getProperty("db.user");
            String password = properties.getProperty("db.password");
            String url = properties.getProperty("db.url");
            connection = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            log.error("Driver class not found", e);
        } catch (IOException e) {
            log.error("Connection property file not found or cant be loaded", e);
        } catch (SQLException e) {
            log.error("Connection can`t be initialised", e);
        }
    }

    private static Properties getProperties() throws IOException {
        Properties properties = new Properties();
        InputStream inputStream = Objects.requireNonNull(ConnectionFactory.class
                                                                 .getClassLoader()
                                                                 .getResourceAsStream(
                                                                         "application.properties"));
        properties.load(inputStream);
        inputStream.close();
        return properties;
    }
}
