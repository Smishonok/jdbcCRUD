package com.valentinnikolaev.jdbccrud.repository.json;

import com.valentinnikolaev.jdbccrud.models.User;
import com.valentinnikolaev.jdbccrud.repository.UserRepository;
import com.valentinnikolaev.jdbccrud.utils.jsonparser.JsonParser;
import com.valentinnikolaev.jdbccrud.utils.jsonparser.JsonParserFactory;
import com.valentinnikolaev.jdbccrud.utils.Constants;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JsonUserRepositoryImpl implements UserRepository {

    private JsonParser<User> parser         = JsonParserFactory.getFactory(User.class).getParser();
    private Path             repositoryPath = Constants.REPOSITORY_PATH.resolve(
            Constants.USER_REPOSITORY_FILE_NAME);

    {
        FileService.createRepository(repositoryPath);
    }

    @Override
    public Optional<User> add(User entity) {
        String repositoryData = FileService.getDataFromRepository(repositoryPath);
        List<User> users = parser.parseList(repositoryData) == null ? new ArrayList<>() :
                parser.parseList(repositoryData);
        users.add(entity);

        String dataForWritingInRepo = parser.serialise(users);
        FileService.writeDataIntoRepository(dataForWritingInRepo, repositoryPath);

        return parser.parseList(FileService.getDataFromRepository(repositoryPath)).stream().filter(
                user->user.equals(entity)).findFirst();
    }

    @Override
    public Optional<User> get(Long aLong) {
        return parser.parseList(FileService.getDataFromRepository(repositoryPath)).stream().filter(
                user->user.getId() == aLong).findFirst();
    }

    @Override
    public Optional<User> change(User entity) {
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
                user->user.getId() == entity.getId()).findFirst();
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