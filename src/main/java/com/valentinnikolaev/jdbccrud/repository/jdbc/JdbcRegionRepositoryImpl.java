package com.valentinnikolaev.jdbccrud.repository.jdbc;

import com.valentinnikolaev.jdbccrud.models.Region;
import com.valentinnikolaev.jdbccrud.repository.RegionRepository;
import com.valentinnikolaev.jdbccrud.utils.ConnectionUtils;
import com.valentinnikolaev.jdbccrud.utils.SQLQueries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Scope ("singleton")
public class JdbcRegionRepositoryImpl implements RegionRepository {

    private final Logger log = LogManager.getLogger();

    @Override
    public Optional<Region> add(Region region) {
        Optional<Region> regionFromDb = Optional.empty();
        try {
            PreparedStatement preparedStatement = ConnectionUtils.getPreparedStatement(
                    SQLQueries.CREATE_REGION.toString());
            preparedStatement.setString(1, region.getName());
            preparedStatement.executeUpdate();

            preparedStatement = ConnectionUtils.getPreparedStatement(
                    SQLQueries.SELECT_REGION_BY_NAME.toString());
            preparedStatement.setString(1, region.getName());

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                regionFromDb = Optional.of(
                        new Region(resultSet.getLong("id"), resultSet.getString("name")));
            }
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            log.error("Region can`t be added into database", e);
        }

        return regionFromDb;
    }

    @Override
    public Optional<Region> get(Long id) {
        Region region = null;
        try {
            PreparedStatement prepareStatement = ConnectionUtils.getPreparedStatement(
                    SQLQueries.SELECT_REGION_BY_ID.toString());
            prepareStatement.setLong(1, id);
            ResultSet resultSet = prepareStatement.executeQuery();
            if (resultSet.next()) {
                region = new Region(id, resultSet.getString("name"));
                resultSet.close();
            }
        } catch (SQLException e) {
            log.error("Region can`t be loaded from database", e);
        }

        return region == null
               ? Optional.empty()
               : Optional.of(region);
    }

    @Override
    public Optional<Region> change(Region region) {
        try {
            PreparedStatement preparedStatement = ConnectionUtils.getPreparedStatement(
                    SQLQueries.UPDATE_REGION.toString());
            preparedStatement.setString(1, region.getName());
            preparedStatement.setLong(2, region.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.error("Region {} can`t be updated", region, e);
        }

        return get(region.getId());
    }

    @Override
    public boolean remove(Long id) {
        try {
            PreparedStatement preparedStatement = ConnectionUtils.getPreparedStatement(
                    SQLQueries.REMOVE_REGION.toString());
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            log.error("Region with id {} can`t be removed", id, e);
        }

        return ! isContains(id);
    }

    @Override
    public List<Region> getAll() {
        List<Region> regionList = new ArrayList<>();
        try {
            Statement statement = ConnectionUtils.getStatement();
            ResultSet resultSet = statement.executeQuery(SQLQueries.SELECT_REGION.toString());

            while (resultSet.next()) {
                long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                regionList.add(new Region(id, name));
            }
            resultSet.close();
        } catch (SQLException e) {
            log.error("Regions can`t be loaded from database", e);
        }

        return regionList;
    }

    @Override
    public boolean removeAll() {
        return ConnectionUtils.removeAllFromTable("regions");
    }

    @Override
    public boolean isContains(Long id) {
        boolean isResultSetNotEmpty = false;
        try {
            PreparedStatement preparedStatement = ConnectionUtils.getPreparedStatement(
                    SQLQueries.SELECT_REGION_BY_ID.toString());
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            isResultSetNotEmpty = resultSet.next();
            resultSet.close();
        } catch (SQLException e) {
            log.error("Region with id {} can`t be loaded", id, e);
        }

        return isResultSetNotEmpty;
    }
}
