package com.valentinnikolaev.jdbccrud.utils;

import java.nio.file.Path;

public class Constants {

    public static String USER_REPOSITORY_FILE_NAME = "users.json";
    public static String POST_REPOSITORY_FILE_NAME = "posts.json";
    public static String REGION_REPOSITORY_FILE_NAME = "regions.json";

    public static Path APP_ROOT_PATH = Path.of(
            ClassLoader.getSystemResource("").getPath().replaceFirst("/", ""));

    public static Path REPOSITORY_PATH = APP_ROOT_PATH.resolve("fileRepository");


}
