package com.valentinnikolaev.jdbccrud.repository.jdbc;

import com.valentinnikolaev.jdbccrud.models.Region;
import com.valentinnikolaev.jdbccrud.models.Role;
import com.valentinnikolaev.jdbccrud.models.User;
import com.valentinnikolaev.jdbccrud.repository.PostRepository;
import com.valentinnikolaev.jdbccrud.repository.UserRepository;
import com.valentinnikolaev.jdbccrud.utils.ConnectionUtils;
import com.valentinnikolaev.jdbccrud.utils.SQLQueries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Component
@Scope ("singleton")
public class JdbcUserRepositoryImpl implements UserRepository {

    private final Logger log = LogManager.getLogger();

    private PostRepository postRepository;

    public JdbcUserRepositoryImpl(@Autowired PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public Optional<User> add(User entity) {
        try {
            PreparedStatement preparedStatement = ConnectionUtils.getPrepareStatement(
                    SQLQueries.CREATE_USER.toString());
            preparedStatement.setString(1, entity.getFirstName());
            preparedStatement.setString(2, entity.getLastName());
            preparedStatement.setLong(3, entity.getRegion().getId());
            preparedStatement.setString(4, entity.getRole().toString());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            log.error("User {} can`t be added into database", entity, e);
        }

        return getUserByFirstAndLastName(entity.getFirstName(), entity.getLastName());
    }

    private Optional<User> getUserByFirstAndLastName(String firstName, String lastName) {
        Optional<User> userFromDb = Optional.empty();
        try {
            PreparedStatement preparedStatement = ConnectionUtils.getPrepareStatement(
                    SQLQueries.SELECT_USER_BY_FIRST_NAME_AND_LAST_NAME.toString());
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                userFromDb = getUserFromResultSet(resultSet);
            }
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            log.error("User with first name: {} and last name: {} cant be loaded from database",
                      firstName, lastName, e);
        }

        return userFromDb;
    }

    private Optional<User> getUserFromResultSet(ResultSet resultSet) throws SQLException {
        long   userId        = resultSet.getLong("id");
        String userFirstName = resultSet.getString("first_name");
        String userLastName  = resultSet.getString("last_name");
        long   regionId      = resultSet.getLong("regions.id");
        String regionName    = resultSet.getString("regions.name");
        Region region        = new Region(regionId, regionName);
        Role   userRole      = Role.valueOf(resultSet.getString("role"));
        return Optional.of(new User(userId, userFirstName, userLastName, region, userRole));
    }

    @Override
    public Optional<User> get(Long id) {
        Optional<User> userFromDb = Optional.empty();
        try {
            PreparedStatement preparedStatement = ConnectionUtils.getPrepareStatement(
                    SQLQueries.SELECT_USER_BY_ID.toString());
            preparedStatement.setLong(1, id);
            ResultSet userResultSet = preparedStatement.executeQuery();
            if (userResultSet.next()) {
                userFromDb = getUserFromResultSet(userResultSet);
            }
            userResultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            log.error("User with id:{} can`t be loaded from database", id, e);
        }
        return userFromDb;
    }

    @Override
    public Optional<User> change(User user) {
        try {
            PreparedStatement preparedStatement = ConnectionUtils.getPrepareStatement(
                    SQLQueries.UPDATE_USER.toString());
            preparedStatement.setString(1, user.getFirstName());
            preparedStatement.setString(2, user.getLastName());
            preparedStatement.setLong(3, user.getRegion().getId());
            preparedStatement.setString(4, user.getRole().toString());
            preparedStatement.setLong(5, user.getId());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            log.error("User with id:{} can`t be updated", user.getId(), e);
        }

        return get(user.getId());
    }

    @Override
    public boolean remove(Long id) {

        try {
            PreparedStatement preparedStatement = ConnectionUtils.getPrepareStatement(
                    SQLQueries.REMOVE_USER.toString());
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            log.error("User with id:{} can`t be removed from database", id, e);
        }

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
