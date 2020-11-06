package com.valentinNikolaev.jdbcCrud.controller;

import com.valentinNikolaev.jdbcCrud.models.User;

import java.util.List;
import java.util.Optional;

public interface UserController {


    User addUser(String firstName, String lastName, String regionName);

    User addUser(String firstName, String lastName, String roleName, String regionName);

    Optional<User> getUserById(String id);

    List<User> getAllUsersList();

    List<User> getUsersWithFirstName(String firstName);

    List<User> getUsersWithLastName(String lastName);

    List<User> getUsersWithRole(String roleName);

    List<User> getUsersFrom(String regionName);

    boolean changeUserFirstName(String userId, String newUserFirstName);

    boolean changeUserLastName(String userId, String newUserLastName);

    boolean changeUserRole(String userId, String newUserRole);

    boolean changeUserRegion(String userId, String regionName);

    boolean removeUser(String userId);

    boolean removeAllUsers();
}
