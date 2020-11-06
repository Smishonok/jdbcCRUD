package com.valentinNikolaev.jdbcCrud.repository.jdbcImpl;

import com.valentinNikolaev.jdbcCrud.models.Region;
import com.valentinNikolaev.jdbcCrud.repository.RegionRepository;
import com.valentinNikolaev.jdbcCrud.utils.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class RegionRepositoryImpl implements RegionRepository {
    @Override
    public Region add(Region region) {
        Function<Connection, Region> transaction = connection->{
            Region regionFromDb = null;
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "insert into jdbccrud.regions(name) values (?)");
                preparedStatement.setString(1, region.getName());
                preparedStatement.executeUpdate();
                connection.commit();

                preparedStatement = connection.prepareStatement(
                        "select * from jdbccrud.regions where name =?");
                preparedStatement.setString(1, region.getName());
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    regionFromDb = new Region(resultSet.getLong("id"), resultSet.getString("name"));
                }

                resultSet.close();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (regionFromDb != null) {
                return regionFromDb;
            } else {
                throw new IllegalArgumentException("Illegal region name.");
            }
        };

        return ConnectionFactory.doTransaction(transaction);
    }

    @Override
    public Region get(Long id) {
        Function<Connection, Region> transaction = connection->{
            Region region = null;
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "select * from jdbccrud.regions where id=?");
                preparedStatement.setLong(1, id);
                preparedStatement.execute();
                ResultSet resultSet = preparedStatement.getResultSet();
                if (resultSet.next()) {
                    region = new Region(id, resultSet.getString("name"));
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (region != null) {
                return region;
            } else {
                throw new IllegalArgumentException(
                        "Region with id " + id + " is not exists in " + "data base");
            }
        };

        return ConnectionFactory.doTransaction(transaction);
    }

    @Override
    public Region change(Region region) {
        Function<Connection, Void> transaction = connection->{
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "update jdbccrud.regions set name=? where id=?");
                preparedStatement.setString(1, region.getName());
                preparedStatement.setLong(2, region.getId());
                preparedStatement.executeUpdate();
                connection.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        };

        ConnectionFactory.doTransaction(transaction);

        return get(region.getId());
    }

    @Override
    public boolean remove(Long id) {
        Function<Connection, Void> transaction = connection->{
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "delete from jdbccrud.regions where id=?");
                preparedStatement.setLong(1, id);
                preparedStatement.executeUpdate();
                connection.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        };

        ConnectionFactory.doTransaction(transaction);

        return ! isContains(id);
    }

    @Override
    public List<Region> getAll() {
        Function<Connection, List<Region>> transaction = connection->{
            List<Region> regionList = new ArrayList<>();
            try {
                Statement statement = connection.createStatement();
                statement.execute("select * from jdbccrud.regions");
                ResultSet resultSet = statement.getResultSet();

                while (resultSet.next()) {
                    long id = resultSet.getLong("id");
                    String name = resultSet.getString("name");
                    regionList.add(new Region(id, name));
                }
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return regionList;
        };


        return ConnectionFactory.doTransaction(transaction);
    }

    @Override
    public boolean removeAll() {
        Function<Connection, Void> transaction = connection->{
            try {
                Statement statement = connection.createStatement();
                statement.executeUpdate("set foreign_key_checks = 0");
                statement.executeUpdate("truncate jdbccrud.regions");
                statement.executeUpdate("set foreign_key_checks =1");
                statement.close();
                connection.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        };

        ConnectionFactory.doTransaction(transaction);

        Function<Connection, Boolean> checkingTransaction = connection->{
            boolean isResultSetEmpty = false;
            try {
                Statement statement = connection.createStatement();
                statement.execute("select * from jdbccrud.regions");
                ResultSet resultSet = statement.getResultSet();
                isResultSetEmpty = ! resultSet.next();
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return isResultSetEmpty;
        };

        return ConnectionFactory.doTransaction(checkingTransaction);
    }

    @Override
    public boolean isContains(Long id) {
        Function<Connection, Boolean> transaction = connection->{
            boolean isResultSetNotEmpty = false;
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "select * from jdbccrud.regions where id=?");
                preparedStatement.setLong(1, id);
                preparedStatement.execute();
                ResultSet resultSet = preparedStatement.getResultSet();
                isResultSetNotEmpty = resultSet.next();
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return isResultSetNotEmpty;
        };

        return ConnectionFactory.doTransaction(transaction);
    }
}
