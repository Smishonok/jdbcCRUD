package com.valentinnikolaev.jdbccrud.repository.json;

import com.valentinnikolaev.jdbccrud.models.Region;
import com.valentinnikolaev.jdbccrud.repository.RegionRepository;
import com.valentinnikolaev.jdbccrud.utils.jsonparser.JsonParser;
import com.valentinnikolaev.jdbccrud.utils.jsonparser.JsonParserFactory;
import com.valentinnikolaev.jdbccrud.utils.Constants;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JsonRegionRepositoryImpl implements RegionRepository {

    private JsonParser<Region> parser         = JsonParserFactory
            .getFactory(Region.class)
            .getParser();
    private Path               repositoryPath = Constants.REPOSITORY_PATH.resolve(
            Constants.REGION_REPOSITORY_FILE_NAME);

    {
        FileService.createRepository(repositoryPath);
    }

    @Override
    public Optional<Region> add(Region entity) {
        String repositoryData = FileService.getDataFromRepository(repositoryPath);
        List<Region> posts = parser.parseList(repositoryData) == null
                             ? new ArrayList<>()
                             : parser.parseList(repositoryData);
        posts.add(entity);

        String dataForWritingInRepo = parser.serialise(posts);
        FileService.writeDataIntoRepository(dataForWritingInRepo, repositoryPath);

        return parser.parseList(FileService.getDataFromRepository(repositoryPath)).stream().filter(
                region->region.equals(entity)).findFirst();
    }

    @Override
    public Optional<Region> get(Long aLong) {
        return parser.parseList(FileService.getDataFromRepository(repositoryPath)).stream().filter(
                region->region.getId() == aLong).findFirst();
    }

    @Override
    public Optional<Region> change(Region entity) {
        String       repositoryData = FileService.getDataFromRepository(repositoryPath);
        List<Region> posts          = parser.parseList(repositoryData);

        posts.stream().filter(region->region.getId() == entity.getId()).forEach(posts::remove);
        posts.add(entity);

        String dataForWritingInRepo = parser.serialise(posts);
        FileService.writeDataIntoRepository(dataForWritingInRepo, repositoryPath);

        return parser.parseList(FileService.getDataFromRepository(repositoryPath)).stream().filter(
                region->region.getId() == entity.getId()).findFirst();
    }

    @Override
    public boolean remove(Long aLong) {
        String       repositoryData = FileService.getDataFromRepository(repositoryPath);
        List<Region> posts          = parser.parseList(repositoryData);

        posts.stream().filter(region->region.getId() == aLong).forEach(posts::remove);
        return parser
                .parseList(FileService.getDataFromRepository(repositoryPath))
                .stream()
                .noneMatch((region->region.getId() == aLong));
    }

    @Override
    public List<Region> getAll() {
        return parser.parseList(FileService.getDataFromRepository(repositoryPath)) == null
               ? new ArrayList<>()
               : parser.parseList(FileService.getDataFromRepository(repositoryPath));
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
                .anyMatch(region->region.getId() == aLong);
    }
}
