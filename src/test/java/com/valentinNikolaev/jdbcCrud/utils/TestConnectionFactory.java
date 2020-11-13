package com.valentinNikolaev.jdbcCrud.utils;

import java.util.Properties;

public class TestConnectionFactory {
    public ConnectionFactory getConnectionFactory() {
        Properties properties = new Properties();
        properties.setProperty("db.user", "root");
        properties.setProperty("db.password", "niscoraguaManagua86");
        properties.setProperty("db.url",
                               "jdbc:mysql://localhost:3306/jdbcCrudTest?serverTimezone=UTC");
        return new ConnectionFactory(properties);
    }
}
