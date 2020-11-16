package com.valentinNikolaev.jdbcCrud.repository.jdbcImpl;

import com.valentinNikolaev.jdbcCrud.models.Region;
import com.valentinNikolaev.jdbcCrud.models.Role;
import com.valentinNikolaev.jdbcCrud.models.User;
import com.valentinNikolaev.jdbcCrud.repository.PostRepository;
import com.valentinNikolaev.jdbcCrud.repository.UserRepository;
import com.valentinNikolaev.jdbcCrud.utils.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Component
@Scope ("singleton")
public class UserRepositoryImpl implements UserRepository {

    private ConnectionFactory connectionFactory;
    private PostRepository    postRepository;

    public UserRepositoryImpl(@Autowired ConnectionFactory connectionFactory,
                              @Autowired PostRepository postRepository) {
        this.connectionFactory = connectionFactory;
        this.postRepository    = postRepository;
    }

    @Override
    public User add(User entity) {
        Function<Connection, Void> transaction = connection->{
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "insert into users (first_name, last_name, region_id, role)" + "VALUES " +
                                "(?,?,?,?)");
                preparedStatement.setString(1, entity.getFirstName());
                preparedStatement.setString(2, entity.getLastName());
                preparedStatement.setLong(3, entity.getRegion().getId());
                preparedStatement.setString(4, entity.getRole().toString());
                preparedStatement.executeUpdate();
                connection.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        };

        connectionFactory.doTransaction(transaction);

        return get(getUserIdByFirstAndLastName(entity.getFirstName(), entity.getLastName()));
    }

    private long getUserIdByFirstAndLastName(String firstName, String lastName) {
        Function<Connection, Long> userTransaction = connection->{
            Long userId = null;
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "select id from " + "users " + "where first_name=? and " + "last_name=?");
                preparedStatement.setString(1, firstName);
                preparedStatement.setString(2, lastName);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    userId = resultSet.getLong(1);
                }
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (userId != null) {
                return userId;
            } else {
                throw new IllegalArgumentException("Illegal user first name or last name. User " +
                                                           "with requested first name and last name is " +
                                                           "not exists in data base.");
            }
        };

        return connectionFactory.doTransaction(userTransaction);
    }

    @Override
    public User get(Long id) {
        Function<Connection, User> userTransaction = connection->{
            User userFromDB = null;
            try {
                PreparedStatement userPreparedStatement = connection.prepareStatement(
                        "select first_name,last_name,region_id,regions.name,role from " +
                                "users left join regions on users.region_id = regions.id where users.id=?");
                userPreparedStatement.setLong(1, id);
                ResultSet userResultSet = userPreparedStatement.executeQuery();

                if (userResultSet.next()) {
                    String firstName = userResultSet.getString(1);
                    String lastName  = userResultSet.getString(2);
                    Region region = new Region(userResultSet.getLong(3),
                                               userResultSet.getString(4));
                    Role role = Role.valueOf(userResultSet.getString(5));

                    userFromDB = new User(id, firstName, lastName, region, role);

                    userFromDB.getPosts().addAll(postRepository.getPostsByUserId(id));
                }
                userResultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (userFromDB != null) {
                return userFromDB;
            } else {
                throw new IllegalArgumentException(
                        "Illegal user id. User with requested id is " +
                                "not exists in data base.");
            }
        };

        return connectionFactory.doTransaction(userTransaction);
    }

    @Override
    public User change(User user) {
        Function<Connection, Void> updateTransaction = connection->{
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "update users set first_name=?,last_name=?,region_id=?,role=? " +
                                "where id=?");
                preparedStatement.setString(1, user.getFirstName());
                preparedStatement.setString(2, user.getLastName());
                preparedStatement.setLong(3, user.getRegion().getId());
                preparedStatement.setString(4, user.getRole().toString());
                preparedStatement.setLong(5, user.getId());
                preparedStatement.executeUpdate();
                connection.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        };

        connectionFactory.doTransaction(updateTransaction);

        return get(user.getId());
    }

    @Override
    public boolean remove(Long id) {
        Function<Connection, Void> userRemoveTransaction = connection->{
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "delete from users where id=?");
                preparedStatement.setLong(1, id);
                preparedStatement.executeUpdate();
                connection.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        };

        connectionFactory.doTransaction(userRemoveTransaction);

        return ! isContains(id);
    }

    @Override
    public List<User> getAll() {
        Function<Connection, List<User>> transaction = connection->{
            List<User> users = new ArrayList<>();
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "select users.id, first_name, last_name, region_id,name, role " + "from" +
                                " users left join regions on users.region_id = regions.id");
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    long   id         = resultSet.getLong("id");
                    String firstName  = resultSet.getString("first_Name");
                    String lastName   = resultSet.getString("last_Name");
                    long   regionId   = resultSet.getLong("region_id");
                    String regionName = resultSet.getString("name");
                    String role       = resultSet.getString("role");

                    User user = new User(id, firstName, lastName, new Region(regionId, regionName),
                                         Role.valueOf(role));

                    user.getPosts().addAll(postRepository.getPostsByUserId(id));
                    users.add(user);
                }
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return users;
        };

        return connectionFactory.doTransaction(transaction);
    }

    @Override
    public boolean removeAll() {
        Function<Connection, Void> transaction = connection->{
            try {
                Statement statement = connection.createStatement();
                statement.executeUpdate("set foreign_key_checks = 0");
                statement.executeUpdate("truncate users");
                statement.executeUpdate("truncate posts");
                statement.executeUpdate("truncate regions");
                statement.executeUpdate("set foreign_key_checks =1");
                connection.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        };

        connectionFactory.doTransaction(transaction);

        Function<Connection, Boolean> isAnyExist = connection->{
            boolean isResultSetNotEmpty = false;
            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("select *from users");
                isResultSetNotEmpty = ! resultSet.next();
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return isResultSetNotEmpty;
        };

        return connectionFactory.doTransaction(isAnyExist);
    }

    @Override
    public boolean isContains(Long id) {
        Function<Connection, Boolean> transaction = connection->{
            boolean isContain = false;
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "select * from users where id=?");
                preparedStatement.setLong(1, id);
                ResultSet resultSet = preparedStatement.executeQuery();
                isContain = resultSet.next();
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return isContain;
        };

        return connectionFactory.doTransaction(transaction);
    }
}
