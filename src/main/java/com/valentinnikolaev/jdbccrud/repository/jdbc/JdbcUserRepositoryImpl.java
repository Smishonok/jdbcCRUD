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
            PreparedStatement preparedStatement = ConnectionUtils.getPreparedStatement(
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
            PreparedStatement preparedStatement = ConnectionUtils.getPreparedStatement(
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
        long userId = resultSet.getLong("users.id");
        String userFirstName = resultSet.getString("first_name");
        String userLastName = resultSet.getString("last_name");
        long regionId = resultSet.getLong("regions.id");
        String regionName = resultSet.getString("regions.name");
        Region region = new Region(regionId, regionName);
        Role userRole = Role.valueOf(resultSet.getString("role"));
        return Optional.of(new User(userId, userFirstName, userLastName, region, userRole));
    }

    @Override
    public Optional<User> get(Long id) {
        Optional<User> userFromDb = Optional.empty();
        try {
            PreparedStatement preparedStatement = ConnectionUtils.getPreparedStatement(
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
            PreparedStatement preparedStatement = ConnectionUtils.getPreparedStatement(
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
            PreparedStatement preparedStatement = ConnectionUtils.getPreparedStatement(
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
        List<User> users = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = ConnectionUtils.getPreparedStatement(
                    SQLQueries.SELECT_USER.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                users.add(getUserFromResultSet(resultSet).get());
            }
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            log.error("Users can`t be loaded from database", e);
        }
        return users;
    }

    @Override
    public boolean removeAll() {
        ConnectionUtils.removeAllFromTable("users");

        boolean isDatabaseEmpty = false;
        try {
            PreparedStatement preparedStatement = ConnectionUtils.getPreparedStatement(
                    SQLQueries.SELECT_USER.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            isDatabaseEmpty = ! resultSet.next();
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            log.error("Users can`t be loaded from database", e);
        }

        return isDatabaseEmpty;
    }

    @Override
    public boolean isContains(Long id) {
        boolean isContain = false;
        try {
            PreparedStatement preparedStatement = ConnectionUtils.getPreparedStatement(
                    SQLQueries.SELECT_USER_BY_ID.toString());
            preparedStatement.setLong(1,id);
            ResultSet resultSet = preparedStatement.executeQuery();
            isContain = resultSet.next();
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            log.error("User can`t be loaded from database", e);
        }
        return isContain;
    }
}
