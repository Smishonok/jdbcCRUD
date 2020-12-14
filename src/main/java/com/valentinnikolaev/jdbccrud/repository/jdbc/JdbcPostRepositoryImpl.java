package com.valentinnikolaev.jdbccrud.repository.jdbc;

import com.valentinnikolaev.jdbccrud.models.Post;
import com.valentinnikolaev.jdbccrud.repository.PostRepository;
import com.valentinnikolaev.jdbccrud.utils.ConnectionUtils;
import com.valentinnikolaev.jdbccrud.utils.SQLQueries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Scope ("singleton")
public class JdbcPostRepositoryImpl implements PostRepository {

    private final Logger log = LogManager.getLogger();

    @Override
    public Optional<Post> add(Post post) {
        try {
            PreparedStatement preparedStatement = ConnectionUtils.getPreparedStatement(
                    SQLQueries.CREATE_POST.toString());
            preparedStatement.setLong(1, post.getUserId());
            preparedStatement.setString(2, post.getContent());
            preparedStatement.setLong(3, post.getDateOfCreation().toEpochSecond(ZoneOffset.UTC));
            preparedStatement.setLong(4, post.getDateOfLastUpdate().toEpochSecond(ZoneOffset.UTC));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.error("Post can`t be added into the data base", e);
        }

        return getPost(post.getUserId(), post.getContent(), post.getDateOfCreation());
    }

    @Override
    public Optional<Post> get(Long id) {
        Post postFromDB = null;
        try {
            PreparedStatement preparedStatement = ConnectionUtils.getPreparedStatement(
                    SQLQueries.SELECT_POST_BY_ID.toString());
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                postFromDB = getPostFromResultSet(resultSet);
            }
            resultSet.close();

        } catch (SQLException e) {
            log.error("Post can`t be loaded from the database", e);
        }

        return postFromDB == null
               ? Optional.empty()
               : Optional.of(postFromDB);
    }

    @Override
    public List<Post> getPostsByUserId(Long userId) {
        List<Post> posts = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = ConnectionUtils.getPreparedStatement(
                    SQLQueries.SELECT_POSTS_BY_USER_ID.toString());
            preparedStatement.setLong(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                posts.add(getPostFromResultSet(resultSet));
            }
            resultSet.close();
        } catch (SQLException e) {
            log.error("User posts can`t be loaded from database", e);
        }

        return posts;
    }

    @Override
    public Optional<Post> change(Post post) {
        try {
            PreparedStatement preparedStatement = ConnectionUtils.getPreparedStatement(
                    SQLQueries.UPDATE_POST.toString());
            preparedStatement.setLong(1, post.getUserId());
            preparedStatement.setString(2, post.getContent());
            preparedStatement.setLong(3, post.getDateOfCreation().toEpochSecond(ZoneOffset.UTC));
            preparedStatement.setLong(4, post.getDateOfLastUpdate().toEpochSecond(ZoneOffset.UTC));
            preparedStatement.setLong(5, post.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.error("Post {} can`t be updated", post, e);
        }

        return get(post.getId());
    }

    @Override
    public boolean remove(Long id) {
        try {
            PreparedStatement preparedStatement = ConnectionUtils.getPreparedStatement(
                    SQLQueries.REMOVE_POST.toString());
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.error("Post with id {} can`t be removes", id, e);
        }

        return isContains(id);
    }

    @Override
    public boolean removePostsByUserId(Long userId) {
        boolean isResultSetEmpty = false;
        try {
            PreparedStatement preparedStatement = ConnectionUtils.getPreparedStatement(
                    SQLQueries.REMOVE_POST_WITH_USER_ID.toString());
            preparedStatement.setLong(1, userId);
            preparedStatement.executeUpdate();

            preparedStatement = ConnectionUtils.getPreparedStatement(
                    SQLQueries.SELECT_POSTS_BY_USER_ID.toString());
            preparedStatement.setLong(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            isResultSetEmpty = ! resultSet.next();
        } catch (SQLException e) {
            log.error("Posts with user`s id {} can`t be removed", userId, e);
        }

        return isResultSetEmpty;
    }

    @Override
    public List<Post> getAll() {
        List<Post> postsList = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = ConnectionUtils.getPreparedStatement(
                    SQLQueries.SELECT_POST.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                postsList.add(getPostFromResultSet(resultSet));
            }
            resultSet.close();
        } catch (SQLException e) {
            log.error("Posts can`t be loaded from database", e);
        }

        return postsList;
    }

    @Override
    public boolean removeAll() {
        return ConnectionUtils.removeAllFromTable("posts");
    }

    @Override
    public boolean isContains(Long id) {
        boolean isResultSetNotEmpty = false;
        try {
            PreparedStatement preparedStatement = ConnectionUtils.getPreparedStatement(
                    SQLQueries.SELECT_POST_BY_ID.toString());
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            isResultSetNotEmpty = resultSet.next();
            resultSet.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

            return isResultSetNotEmpty;
    }

    private Optional<Post> getPost(long userId, String content, LocalDateTime dateOfCreation) {
        Optional<Post> postFromDB = Optional.empty();
        try {
            PreparedStatement preparedStatement = ConnectionUtils.getPreparedStatement(
                    SQLQueries.SELECT_POST_BY_USER_ID_CONTENT_DATE_OF_CREATION.toString());
            preparedStatement.setLong(1, userId);
            preparedStatement.setString(2, content);
            preparedStatement.setLong(3, dateOfCreation.toEpochSecond(ZoneOffset.UTC));
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                postFromDB = Optional.of(getPostFromResultSet(resultSet));
            }
            resultSet.close();
        } catch (SQLException e) {
            log.error("Post can`t be loaded from database", e);
        }

        return postFromDB;
    }

    private Post getPostFromResultSet(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("id");
        long userIdFromDB = resultSet.getLong("user_id");
        String contentFromDB = resultSet.getString("content");
        LocalDateTime creatingDate = LocalDateTime.ofEpochSecond(resultSet.getLong("creating_date"),
                                                                 0, ZoneOffset.UTC);
        LocalDateTime updatingDate = LocalDateTime.ofEpochSecond(resultSet.getLong("updating_date"),
                                                                 0, ZoneOffset.UTC);

        return new Post(id, userIdFromDB, contentFromDB, creatingDate, updatingDate);
    }
}
