package com.valentinNikolaev.jdbcCrud.repository.jdbcImpl;

import com.valentinNikolaev.jdbcCrud.models.Post;
import com.valentinNikolaev.jdbcCrud.models.Region;
import com.valentinNikolaev.jdbcCrud.repository.PostRepository;
import com.valentinNikolaev.jdbcCrud.utils.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Component
@Scope("singleton")
public class PostRepositoryImpl implements PostRepository {

    private ConnectionFactory connectionFactory;

    public PostRepositoryImpl(@Autowired ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public List<Post> getPostsByUserId(Long userId) {
        Function<Connection, List<Post>> transaction = connection->{
            List<Post> posts = new ArrayList<>();
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "select * from jdbccrud.posts where user_id=?");
                preparedStatement.setLong(1, userId);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    long id = resultSet.getLong("id");
                    long user_id = resultSet.getLong("user_id");
                    String content = resultSet.getString("content");
                    LocalDateTime createdDate = LocalDateTime.ofEpochSecond(
                            resultSet.getLong("created_date"), 0, ZoneOffset.UTC);
                    LocalDateTime updateDate = LocalDateTime.ofEpochSecond(
                            resultSet.getLong("update_date"), 0, ZoneOffset.UTC);

                    posts.add(new Post(id, user_id, content, createdDate, updateDate));
                }
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return posts;
        };

        return connectionFactory.doTransaction(transaction);
    }

    @Override
    public boolean removePostsByUserId(Long userId) {
        Function<Connection, Void> transaction = connection->{
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "delete from jdbccrud.posts where user_id=?");
                preparedStatement.setLong(1, userId);
                preparedStatement.executeUpdate();
                connection.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        };

        Function<Connection, Boolean> checkRequestTransaction = connection->{
            boolean isResultSetEmpty = false;
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "select * from jdbccrud.posts where user_id=?");
                preparedStatement.setLong(1, userId);
                ResultSet resultSet = preparedStatement.getResultSet();
                isResultSetEmpty = ! resultSet.next();
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return isResultSetEmpty;
        };

        return connectionFactory.doTransaction(checkRequestTransaction);
    }

    @Override
    public Post add(Post post) {
        Function<Connection, Void> transaction = connection->{
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "insert into jdbccrud.posts(user_id, content, creating_date, " +
                        "updating_date) " + "values (?,?,?,?)");
                preparedStatement.setLong(1, post.getUserId());
                preparedStatement.setString(2, post.getContent());
                preparedStatement.setLong(3,
                                          post.getDateOfCreation().toEpochSecond(ZoneOffset.UTC));
                preparedStatement.setLong(4, post
                        .getDateOfLastUpdate()
                        .toEpochSecond(ZoneOffset.UTC));
                preparedStatement.executeUpdate();
                connection.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        };

        connectionFactory.doTransaction(transaction);

        return getPostByUserIdContentAndDate(post.getUserId(), post.getContent(),
                                             post.getDateOfCreation());
    }

    private Post getPostByUserIdContentAndDate(long userId, String content,
                                               LocalDateTime dateOfCreation) {
        Function<Connection, Post> transaction = connection->{
            Post postFromDB = null;
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "select * from jdbccrud.posts where user_id=? and content=? and " +
                        "creating_date=?");
                preparedStatement.setLong(1, userId);
                preparedStatement.setString(2, content);
                preparedStatement.setLong(3, dateOfCreation.toEpochSecond(ZoneOffset.UTC));
                ResultSet resultSet = preparedStatement.getResultSet();
                if (resultSet.next()) {
                    postFromDB = getPostFromResultSet(resultSet);
                }
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (postFromDB != null) {
                return postFromDB;
            } else {
                throw new IllegalArgumentException("Illegal post parameters, post with requested " +
                                                   "parameters is not exists in date base.");
            }
        };

        return connectionFactory.doTransaction(transaction);
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

    @Override
    public Post get(Long id) {
        Function<Connection, Post> transaction = connection->{
            Post postFromDB = null;
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "select * from jdbccrud.posts where posts.id=?");
                preparedStatement.setLong(1, id);
                ResultSet resultSet = preparedStatement.getResultSet();
                if (resultSet.next()) {
                    postFromDB = getPostFromResultSet(resultSet);
                }
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (postFromDB != null) {
                return postFromDB;
            } else {
                throw new IllegalArgumentException(
                        "Post with id " + id + " is not exists in " + "data base");
            }
        };

        return connectionFactory.doTransaction(transaction);
    }

    @Override
    public Post change(Post post) {
        Function<Connection, Void> transaction = connection->{
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "update jdbccrud.posts set user_id=?,content=?,creating_date=?, " +
                        "updating_date=? where id=?");
                preparedStatement.setLong(1, post.getUserId());
                preparedStatement.setString(2, post.getContent());
                preparedStatement.setLong(3,
                                          post.getDateOfCreation().toEpochSecond(ZoneOffset.UTC));
                preparedStatement.setLong(4,post.getDateOfLastUpdate().toEpochSecond(ZoneOffset.UTC));
                preparedStatement.setLong(5,post.getId());
                preparedStatement.executeUpdate();
                connection.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            } return null;
        };

        connectionFactory.doTransaction(transaction);

        return get(post.getId());
    }

    @Override
    public boolean remove(Long id) {
        return false;
    }

    @Override
    public List<Post> getAll() {
        Function<Connection, List<Post>> transaction = connection->{
            List<Post> postsList = new ArrayList<>();
            try {
                Statement statement = connection.createStatement();
                statement.execute("select * from jdbccrud.posts");
                ResultSet resultSet = statement.getResultSet();

                while (resultSet.next()) {
                    postsList.add(getPostFromResultSet(resultSet));
                }
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return postsList;
        };


        return connectionFactory.doTransaction(transaction);
    }

    @Override
    public boolean removeAll() {
        Function<Connection, Void> transaction = connection->{
            try {
                Statement statement = connection.createStatement();
                statement.execute("truncate jdbccrud.posts");
                connection.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        };

        connectionFactory.doTransaction(transaction);

        Function<Connection, Boolean> checkingTransaction = connection->{
            boolean isResultSetEmpty = false;
            try {
                Statement statement = connection.createStatement();
                statement.execute("select * from jdbccrud.posts");
                ResultSet resultSet = statement.getResultSet();
                isResultSetEmpty = ! resultSet.next();
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return isResultSetEmpty;
        };

        return connectionFactory.doTransaction(checkingTransaction);
    }

    @Override
    public boolean isContains(Long id) {
        Function<Connection, Boolean> transaction = connection->{
            boolean isResultSetEmpty = false;
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "select * from jdbccrud.posts where id=?");
                preparedStatement.setLong(1, id);
                ResultSet resultSet = preparedStatement.getResultSet();
                isResultSetEmpty = ! resultSet.next();
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return isResultSetEmpty;
        };

        return connectionFactory.doTransaction(transaction);
    }
}
