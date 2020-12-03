package com.valentinnikolaev.jdbccrud.utils;

public enum SQLQueries {
    CREATE_USER("insert into users (first_name, last_name, region_id, role) VALUES (?,?,?,?)"),
    CREATE_POST("insert into posts(user_id, content, creating_date, dating_date) values (?,?,?,?)"),
    SELECT_POST("select * from posts"), SELECT_POST_BY_ID("select * from posts where posts.id=?"),
    SELECT_POSTS_BY_USER_ID("select * from posts where user_id=?"),
    UPDATE_POST("update posts set user_id=?,content=?,creating_date=?, updating_date=? where id=?"),
    REMOVE_POST("delete from posts where id=?"),
    REMOVE_POST_WITH_USER_ID("delete from posts where user_id=?"),
    SELECT_POST_BY_USER_ID_CONTENT_DATE_OF_CREATION(
            "select * from posts where user_id=? and content=? and creating_date=?");

    private String value;

    SQLQueries(String value) {
        this.value = value;
    }
}
