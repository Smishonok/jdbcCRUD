package com.valentinnikolaev.jdbccrud.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionUtils {

    private static final Logger log = LogManager.getLogger();

    public static Statement getStatement() throws SQLException {
        return ConnectionFactory.getConnection().createStatement();
    }

    public static PreparedStatement getPreparedStatement(String sqlQuery) throws SQLException {
        return ConnectionFactory.getConnection().prepareStatement(sqlQuery);
    }

    public static int execute(String sqlQuery) throws SQLException {
        return ConnectionFactory.getConnection().createStatement().executeUpdate(sqlQuery);
    }

    public static boolean removeAllFromTable(String tableName) {
        boolean isResultSetEmpty = false;
        try {
            PreparedStatement preparedStatement = ConnectionUtils.getPreparedStatement(
                    SQLQueries.FOREIGN_KEY_CHECKS.toString());
            preparedStatement.setLong(1,  1);
            preparedStatement.executeUpdate();

            preparedStatement = ConnectionUtils.getPreparedStatement(SQLQueries.TRUNCATE.toString());
            preparedStatement.setString(1,tableName);
            preparedStatement.executeUpdate();

            preparedStatement = ConnectionUtils.getPreparedStatement(
                    SQLQueries.FOREIGN_KEY_CHECKS.toString());
            preparedStatement.setString(1, "1");
            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.executeQuery(SQLQueries.SELECT_POST.toString());
            isResultSetEmpty = ! resultSet.next();
            resultSet.close();
        } catch (SQLException e) {
            log.error("Removing statement can`t be executed", e);
        }

        return isResultSetEmpty;
    }

}
