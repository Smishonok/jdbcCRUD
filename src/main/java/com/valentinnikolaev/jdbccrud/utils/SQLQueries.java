package com.valentinnikolaev.jdbccrud.utils;

public enum SQLQueries {
    CREATE_USER(
            "insert into users (first_name, last_name, region_id, role) VALUES (?,?,?,?)"), SELECT_USER(
            "select * from users left join regions on users.region_id = regions.id"), SELECT_USER_BY_FIRST_NAME_AND_LAST_NAME(
            "select * from users left join regions on users.region_id = regions.id where " +
                    "first_name=? and last_name=?"), SELECT_USER_BY_ID(
            "select users.id,first_name,last_name,regions.id,regions.name,role from users " +
            "left join regions on users.region_id = regions.id where users.id=?"), UPDATE_USER(
            "update users set first_name=?,last_name=?,region_id=?,role=? where id=?"), REMOVE_USER(
            "delete from users where id=?"), CREATE_REGION(
            "insert into regions(name) values (?)"), SELECT_REGION(
            "select * from regions"), SELECT_REGION_BY_NAME(
            "select * from regions where name =?"), SELECT_REGION_BY_ID(
            "select * from regions where id=?"), CREATE_POST(
            "insert into posts(user_id, content, creating_date, updating_date) values (?,?,?,?)"),
    SELECT_POST(
            "select * from posts"), SELECT_POST_BY_ID(
            "select * from posts where posts.id=?"), SELECT_POSTS_BY_USER_ID(
            "select * from posts where user_id=?"), UPDATE_POST(
            "update posts set user_id=?,content=?,creating_date=?, updating_date=? where id=?"), UPDATE_REGION(
            "update regions set name=? where id=?"), REMOVE_REGION(
            "delete from regions where id=?"), REMOVE_POST(
            "delete from posts where id=?"), REMOVE_POST_WITH_USER_ID(
            "delete from posts where user_id=?"), FOREIGN_KEY_CHECKS(
            "set foreign_key_checks = ?"), TRUNCATE(
            "truncate ?"), SELECT_POST_BY_USER_ID_CONTENT_DATE_OF_CREATION(
            "select * from posts where user_id=? and content=? and creating_date=?");

    private String value;

    SQLQueries(String value) {
        this.value = value;
    }


    @Override
    public String toString() {
        return value;
    }
}
