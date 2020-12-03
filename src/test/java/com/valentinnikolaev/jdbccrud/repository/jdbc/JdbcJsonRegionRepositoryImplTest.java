package com.valentinnikolaev.jdbccrud.repository.jdbc;

import com.valentinnikolaev.jdbccrud.models.Region;
import com.valentinnikolaev.jdbccrud.repository.RegionRepository;
import com.valentinnikolaev.jdbccrud.utils.TestConnection;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.*;

@TestInstance (TestInstance.Lifecycle.PER_CLASS)
@DisplayName ("Tests for region dao class with JDBC implementation")
class JdbcJsonRegionRepositoryImplTest {

    private TestConnection   testConnection   = new TestConnection();
    private RegionRepository regionRepository = new JdbcRegionRepositoryImpl(
            testConnection.getConnectionFactory());

    @BeforeAll
    public void cleanDataBaseBeforeAll() {
        testConnection.cleanDataBase();
    }

    @AfterEach
    public void cleanDataBaseAfterTest() {
        testConnection.cleanDataBase();
    }

    private Region getRegionFromDB(String regionName) {
        Function<Connection, Region> transaction = connection->{
            Region region = null;
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "select * from regions where name=?");
                preparedStatement.setString(1, regionName);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    region = getRegion(resultSet);
                }
                resultSet.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return region;
        };

        return testConnection.doTransaction(transaction);
    }

    private List<Region> getAllRegions() {
        Function<Connection, List<Region>> transaction = connection->{
            List<Region> regions = new ArrayList<>();
            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("select * from regions");
                while (resultSet.next()) {
                    regions.add(getRegion(resultSet));
                }
                resultSet.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return regions;
        };
        return testConnection.doTransaction(transaction);
    }

    private Region getRegion(ResultSet resultSet) throws SQLException {
        long   id   = resultSet.getLong("id");
        String name = resultSet.getString("name");
        return new Region(id, name);
    }

    @Nested
    @DisplayName ("Tests for the add method")
    class AddMethodsTest {

        @Test
        @DisplayName ("When region added into database then it can be get")
        public void whenAddRegionThenRegionCanBeGetFromDataBase() {
            //given
            Region expectedRegion = new Region(1, "TestRegion");

            //when
            regionRepository.add(expectedRegion);

            //then
            Region actualRegion = getRegionFromDB(expectedRegion.getName());
            assertThat(actualRegion).extracting("name").isEqualTo(actualRegion.getName());
        }

        @Test
        @DisplayName ("When add region then return region from database")
        public void whenAddRegionThenReturnAddedRegion() {
            //given
            Region expectedRegion = new Region(1, "TestRegion");

            //when
            Region actualRegion = regionRepository.add(expectedRegion);

            //then
            assertThat(actualRegion).extracting("name").isEqualTo(expectedRegion.getName());
        }
    }


    @Nested
    @DisplayName ("Tests for the get method")
    class GetMethodTests {

        @Test
        @DisplayName ("When get region and region exist in database then return region")
        public void whenGetRegionWhichExistInDBThenReturnRequestedRegion() {
            //given
            Region expectedRegion = new Region(1, "TestRegion");
            regionRepository.add(expectedRegion);
            long regionFromDbId = getRegionFromDB(expectedRegion.getName()).getId();

            //when
            Region actualRegion = regionRepository.get(regionFromDbId);

            //then
            assertThat(actualRegion).extracting("name").isEqualTo(expectedRegion.getName());
        }

        @Test
        @DisplayName ("When get region which not exist in database then throw exception")
        public void whenGetRegionWhichNotExistInDbThenThrowException() {
            //when
            Throwable throwable = catchThrowable(()->regionRepository.get(154l));

            //then
            assertThat(throwable).isInstanceOf(IllegalArgumentException.class).hasMessageContaining(
                    "Region with id");
        }
    }


    @Nested
    @DisplayName ("Tests for the change method")
    class ChangeMethodTests {

        @Test
        @DisplayName ("When change the region then region chang in database")
        public void whenChangeRegionThenRegionChangingInDataBase() {
            //given
            Region regionBeforeChanging = new Region(1, "TestRegion");
            regionRepository.add(regionBeforeChanging);

            //when
            Region expectedRegion = getRegionFromDB(regionBeforeChanging.getName());
            expectedRegion.setName("ChangedName");
            regionRepository.change(expectedRegion);

            //then
            Region actualRegion = getRegionFromDB(expectedRegion.getName());
            assertThat(actualRegion).extracting("name").isEqualTo(expectedRegion.getName());
        }

        @Test
        @DisplayName ("When change the region then return changed region")
        public void whenChangeRegionThenReturnChangedRegion() {
            //given
            Region regionBeforeChanging = new Region(1, "TestRegion");
            regionRepository.add(regionBeforeChanging);

            //when
            Region expectedRegion = getRegionFromDB(regionBeforeChanging.getName());
            expectedRegion.setName("ChangedName");
            Region actualRegion = regionRepository.change(expectedRegion);

            //then
            assertThat(actualRegion).extracting("name").isEqualTo(expectedRegion.getName());
        }
    }

    @Nested
    @DisplayName ("Tests for the remove method")
    class RemoveMethodTests {

        @Test
        @DisplayName ("When remove region then region is not exist in database")
        public void whenRemoveRegionThenRegionIsNotExistInDB() {
            //given
            Region targetRegion    = new Region(1, "TestRegion");
            Region nonTargetRegion = new Region(2, "TestRegion2");
            regionRepository.add(targetRegion);
            regionRepository.add(nonTargetRegion);

            //when
            long regionID = getRegionFromDB(targetRegion.getName()).getId();
            regionRepository.remove(regionID);

            //then
            List<Region> actualRegions = getAllRegions();
            assertThat(actualRegions).extracting("name").doesNotContain(targetRegion.getName());
        }
    }

    @Nested
    @DisplayName ("Tests for the getAll method")
    class GetAllMethodTests {

        @Test
        @DisplayName ("When get all regions then return all regions from database")
        public void whenGetAllThenReturnAllRegionsInDatabase() {
            //given
            List<Region> expectedRegions = List.of(new Region(1, "TestRegion"),
                                                   new Region(2, "TestRegion2"),
                                                   new Region(3, "TestRegion3"));
            expectedRegions.forEach(regionRepository::add);

            //when
            List<Region> actualRegions = regionRepository.getAll();

            //then
            assertThat(actualRegions).containsSequence(expectedRegions);
        }
    }

    @Nested
    @DisplayName ("Tests for the removeAll method")
    class RemoveAllMethodTests {

        @Test
        @DisplayName ("When removeAll then database is empty")
        public void whenRemoveAllThenDatabaseIsEmpty() {
            //given
            List<Region> regions = List.of(new Region(1, "TestRegion"),
                                           new Region(2, "TestRegion2"),
                                           new Region(3, "TestRegion3"));
            regions.forEach(regionRepository::add);

            //when
            regionRepository.removeAll();

            //then
            List<Region> actualRegionsList = getAllRegions();
            assertThat(actualRegionsList).isEmpty();
        }
    }



}
