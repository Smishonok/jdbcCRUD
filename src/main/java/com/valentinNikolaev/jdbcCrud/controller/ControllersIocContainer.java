package com.valentinNikolaev.jdbcCrud.controller;

import com.valentinNikolaev.jdbcCrud.repository.jdbcImpl.PostRepositoryImpl;
import com.valentinNikolaev.jdbcCrud.repository.jdbcImpl.RegionRepositoryImpl;
import com.valentinNikolaev.jdbcCrud.repository.jdbcImpl.UserRepositoryImpl;

public class ControllersIocContainer {

    private static PostControllerImpl postController;
    private static UserControllerImpl userController;
    private static RegionControllerImpl regionController;

    static {
        postController   = new PostControllerImpl(new PostRepositoryImpl());
        userController   = new UserControllerImpl(new UserRepositoryImpl());
        regionController = new RegionControllerImpl(new RegionRepositoryImpl());

        postController.setUserController(userController);
        userController.setRegionController(regionController);
    }

    public static PostController getPostController() {
        return postController;
    }

    public static UserController getUserController() {
        return userController;
    }

    public static RegionController getRegionController() {
        return regionController;
    }
}
