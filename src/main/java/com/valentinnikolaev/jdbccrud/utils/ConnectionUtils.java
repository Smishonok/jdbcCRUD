package com.valentinnikolaev.jdbccrud.utils;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ConnectionUtils {

    public static PreparedStatement getPrepareStatement(String sqlQuery) throws SQLException {
        return ConnectionFactory.getConnection().prepareStatement(sqlQuery);
    }

    public static int execute(String sqlQuery) throws SQLException {
        return ConnectionFactory.getConnection().createStatement().executeUpdate(sqlQuery);
    }

}
