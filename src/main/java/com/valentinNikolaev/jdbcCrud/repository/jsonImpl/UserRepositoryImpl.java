package com.valentinNikolaev.jdbcCrud.repository.jsonImpl;

import com.valentinNikolaev.jdbcCrud.models.User;
import com.valentinNikolaev.jdbcCrud.repository.UserRepository;
import com.valentinNikolaev.jdbcCrud.service.jsonParser.JsonParser;
import com.valentinNikolaev.jdbcCrud.service.jsonParser.JsonParserFactory;
import com.valentinNikolaev.jdbcCrud.utils.Constants;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserRepositoryImpl implements UserRepository {

    private JsonParser<User> parser         = JsonParserFactory.getFactory(User.class).getParser();
    private Path             repositoryPath = Constants.REPOSITORY_PATH.resolve(
            Constants.USER_REPOSITORY_FILE_NAME);

    {
        FileService.createRepository(repositoryPath);
    }

    @Override
    public User add(User entity) {
        String repositoryData = FileService.getDataFromRepository(repositoryPath);
        List<User> users = parser.parseList(repositoryData) == null ? new ArrayList<>() :
                parser.parseList(repositoryData);
        users.add(entity);

        String dataForWritingInRepo = parser.serialise(users);
        FileService.writeDataIntoRepository(dataForWritingInRepo, repositoryPath);

        return parser.parseList(FileService.getDataFromRepository(repositoryPath)).stream().filter(
                user->user.equals(entity)).findFirst().get();
    }

    @Override
    public User get(Long aLong) {
        return parser.parseList(FileService.getDataFromRepository(repositoryPath)).stream().filter(
                user->user.getId() == aLong).findFirst().get();
    }

    @Override
    public User change(User entity) {
        String     repositoryData = FileService.getDataFromRepository(repositoryPath);
        List<User> users          = parser.parseList(repositoryData);

        users
                .stream()
                .filter(user->user.getId() == entity.getId())
                .collect(Collectors.toList())
                .forEach(users::remove);
        users.add(entity);

        String dataForWritingInRepo = parser.serialise(users);
        FileService.writeDataIntoRepository(dataForWritingInRepo, repositoryPath);

        return parser.parseList(FileService.getDataFromRepository(repositoryPath)).stream().filter(
                user->user.getId() == entity.getId()).findFirst().get();
    }

    @Override
    public boolean remove(Long aLong) {
        String     repositoryData = FileService.getDataFromRepository(repositoryPath);
        List<User> users          = parser.parseList(repositoryData);

        users.stream().filter(user->user.getId() == aLong).forEach(users::remove);
        return parser
                .parseList(FileService.getDataFromRepository(repositoryPath))
                .stream()
                .noneMatch((user->user.getId() == aLong));
    }

    @Override
    public List<User> getAll() {
        return parser.parseList(FileService.getDataFromRepository(repositoryPath)) == null ?
                new ArrayList<>() : parser.parseList(
                FileService.getDataFromRepository(repositoryPath));
    }

    @Override
    public boolean removeAll() {
        String dataForWritingInRepo = parser.serialise(new ArrayList<>());
        FileService.writeDataIntoRepository(dataForWritingInRepo, repositoryPath);
        return parser.parseList(FileService.getDataFromRepository(repositoryPath)).isEmpty();
    }

    @Override
    public boolean isContains(Long aLong) {
        return parser
                .parseList(FileService.getDataFromRepository(repositoryPath))
                .stream()
                .anyMatch(user->user.getId() == aLong);
    }
}