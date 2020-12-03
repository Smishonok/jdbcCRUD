package com.valentinnikolaev.jdbccrud.utils;

import com.valentinnikolaev.jdbccrud.models.Post;
import com.valentinnikolaev.jdbccrud.models.Region;
import com.valentinnikolaev.jdbccrud.models.Role;
import com.valentinnikolaev.jdbccrud.models.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
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

    public User addUserIntoDB(String firstName, String lastName, long regionID, Role role) {
        Function<Connection, User> transaction = connection->{
            User user = null;
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "insert into users (first_name, last_name, region_id, role) " +
                        "VALUE (?,?,?,?)");
                preparedStatement.setString(1, firstName);
                preparedStatement.setString(2, lastName);
                preparedStatement.setLong(3, regionID);
                preparedStatement.setString(4, role.toString());
                preparedStatement.executeUpdate();
                connection.commit();

                preparedStatement = connection.prepareStatement(
                        "select * from users left join regions on users.region_id = regions.id where " +
                        "first_name=? " + "and last_name=?");
                preparedStatement.setString(1, firstName);
                preparedStatement.setString(2, lastName);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    user = getUser(resultSet);
                }
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return user;
        };
        return doTransaction(transaction);
    }

    public User addUserIntoDB(User user) {
        return addUserIntoDB(user.getFirstName(), user.getLastName(), user.getRegion().getId(),
                      user.getRole());
    }

    public User getUserFromDb(String firstName, String lastName) {
        Function<Connection, User> transaction = connection->{
            User user = null;
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "select * from users left join regions on users.region_Id=regions.id " +
                        "where first_name=? and last_name=?");
                preparedStatement.setString(1, firstName);
                preparedStatement.setString(2, lastName);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    user = getUser(resultSet);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return user;
        };

        return doTransaction(transaction);
    }

    public List<User> getAllUsersFromDB() {
        Function<Connection, List<User>> transaction = connection->{
            List<User> users = new ArrayList<>();

            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(
                        "select * from users left join regions on users.region_id " +
                        "= regions.id");
                while (resultSet.next()) {
                    users.add(getUser(resultSet));
                }
                resultSet.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return users;
        };

        return doTransaction(transaction);
    }

    private User getUser(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("users.id");
        String userFirstName = resultSet.getString("first_name");
        String userLastName = resultSet.getString("last_name");
        Long regionId = resultSet.getLong("regions.id");
        String regionName = resultSet.getString("regions.name");
        Role userRole = Role.valueOf(resultSet.getString("role"));
        return new User(id, userFirstName, userLastName, new Region(regionId, regionName),
                        userRole);
    }

    public Region addRegionIntoDB(String regionName) {
        Function<Connection, Region> transaction = connection->{
            Region regionFromDb = null;
            PreparedStatement preparedStatement = null;
            try {
                preparedStatement = connection.prepareStatement(
                        "insert into regions(name) VALUE (?)");
                preparedStatement.setString(1, regionName);
                preparedStatement.executeUpdate();
                connection.commit();

                preparedStatement = connection.prepareStatement(
                        "select * from regions where name=?");
                preparedStatement.setString(1, regionName);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    regionFromDb = getRegion(resultSet);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return regionFromDb;
        };

        return doTransaction(transaction);
    }

    public Region getRegionFromDB(String regionName) {
        Function<Connection, Region> transaction = connection->{
            Region region = null;
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "select * from regions where name=?");
                preparedStatement.setString(1, regionName);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    region = getRegion(resultSet);
                }
                resultSet.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return region;
        };

        return doTransaction(transaction);
    }

    public List<Region> getAllRegions() {
        Function<Connection, List<Region>> transaction = connection->{
            List<Region> regions = new ArrayList<>();
            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("select * from regions");
                while (resultSet.next()) {
                    regions.add(getRegion(resultSet));
                }
                resultSet.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return regions;
        };
        return doTransaction(transaction);
    }

    private Region getRegion(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("id");
        String name = resultSet.getString("name");
        return new Region(id, name);
    }

    public Post addPostIntoDB(String content, long userId, long creatingDate, long updatingDate) {

        Function<Connection, Post> transaction = connection->{
            Post post = null;
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "insert into posts (user_id, content, creating_date, updating_date) " +
                        "value(?,?,?,?)");
                preparedStatement.setLong(1, userId);
                preparedStatement.setString(2, content);
                preparedStatement.setLong(3, creatingDate);
                preparedStatement.setLong(4, updatingDate);
                preparedStatement.executeUpdate();
                connection.commit();

                preparedStatement = connection.prepareStatement(
                        "select * from posts left join " + "users on posts.user_id = users.id " +
                        "where content=? and users.id=? and " + "creating_date=?");
                preparedStatement.setLong(2, userId);
                preparedStatement.setString(1, content);
                preparedStatement.setLong(3, creatingDate);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    post = getPost(resultSet);
                }
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return post;
        };

        return doTransaction(transaction);
    }

    public Post addPostIntoDB(Post post) {
        return addPostIntoDB(post.getContent(), post.getUserId(),
                             post.getDateOfCreation().toEpochSecond(ZoneOffset.UTC),
                             post.getDateOfLastUpdate().toEpochSecond(ZoneOffset.UTC));
    }

    public Post getPostFromDB(User user, String content, LocalDateTime timeOfCreating) {
        Function<Connection, Post> transaction = connection->{
            Post post = null;
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "select * from posts left join users on posts.user_id = " +
                        "users.id where user_id=? and content=? and " + "creating_date=?");
                preparedStatement.setLong(1, user.getId());
                preparedStatement.setString(2, content);
                preparedStatement.setLong(3, timeOfCreating.toEpochSecond(ZoneOffset.UTC));
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    post = getPost(resultSet);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return post;
        };

        return doTransaction(transaction);
    }

    public List<Post> getAllPosts() {
        Function<Connection, List<Post>> transaction = connection->{
            List<Post> posts = new ArrayList<>();
            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("select * from posts");
                while (resultSet.next()) {
                    posts.add(getPost(resultSet));
                }
                resultSet.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return posts;
        };
        return doTransaction(transaction);
    }

    private Post getPost(ResultSet resultSet) throws SQLException {
        long postId = resultSet.getLong("id");
        String content = resultSet.getString("content");
        long userId = resultSet.getLong("user_id");
        LocalDateTime creatingDate = LocalDateTime.ofEpochSecond(resultSet.getLong("creating_date"),
                                                                 0, ZoneOffset.UTC);
        LocalDateTime updatingDate = LocalDateTime.ofEpochSecond(resultSet.getLong("updating_date"),
                                                                 0, ZoneOffset.UTC);
        return new Post(postId, userId, content, creatingDate, updatingDate);
    }

}
