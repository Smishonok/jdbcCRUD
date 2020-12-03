package com.valentinnikolaev.jdbccrud.repository.json;

import com.valentinnikolaev.jdbccrud.utils.Constants;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.stream.Collectors;

public class FileService {

    public static void createRepository(Path repositoryPath) {
        if (! Files.exists(Constants.REPOSITORY_PATH)) {
            try {
                Files.createDirectory(Constants.REPOSITORY_PATH);
            } catch (IOException e) {
                throw new IllegalStateException("User repository file cant be created.", e);
            }
        }

        if (! Files.exists(repositoryPath)) {
            try {
                Files.createFile(repositoryPath);
            } catch (IOException e) {
                throw new IllegalStateException("User repository file cant be created.", e);
            }
        }
    }

    public static String getDataFromRepository(Path repositoryPath) {
        try {
            return Files.lines(repositoryPath).collect(Collectors.joining());
        } catch (IOException e) {
            throw new IllegalStateException("Repository file can`t be read.", e);
        }
    }

    public static void writeDataIntoRepository(String data, Path repositoryPath) {
        try {
            Files.write(repositoryPath, data.getBytes(), StandardOpenOption.WRITE,
                        StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Data can`t be written into repository.", e);
        }
    }



}
