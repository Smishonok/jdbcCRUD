package com.valentinNikolaev.jdbcCrud.repository.jdbcImpl;

import com.valentinNikolaev.jdbcCrud.models.Region;
import com.valentinNikolaev.jdbcCrud.models.Role;
import com.valentinNikolaev.jdbcCrud.models.User;
import com.valentinNikolaev.jdbcCrud.repository.PostRepository;
import com.valentinNikolaev.jdbcCrud.repository.RegionRepository;
import com.valentinNikolaev.jdbcCrud.repository.UserRepository;
import com.valentinNikolaev.jdbcCrud.utils.TestConnection;
import org.junit.jupiter.api.*;

import java.lang.management.ThreadInfo;
import java.rmi.server.ExportException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

@TestInstance (TestInstance.Lifecycle.PER_CLASS)
@DisplayName ("Tests for user DAO class based on JDBC")
class UserRepositoryImplTest {

    private TestConnection   testConnection   = new TestConnection();
    private PostRepository   postRepository   = new PostRepositoryImpl(
            testConnection.getConnectionFactory());
    private UserRepository   userRepository   = new UserRepositoryImpl(
            testConnection.getConnectionFactory(), postRepository);
    private RegionRepository regionRepository = new RegionRepositoryImpl(
            testConnection.getConnectionFactory());

    @BeforeAll
    public void cleanDataBaseBeforeAllTest() {
        testConnection.cleanDataBase();
    }

    @AfterEach
    public void cleanDataBaseAfterTests() {
        testConnection.cleanDataBase();
    }

    private User getUserFromDb(String firstName, String lastName) {
        Function<Connection, User> transaction = connection->{
            User user = null;
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "select * from users left join regions on users.region_Id=regions.id " +
                                "where first_name=? and last_name=?");
                preparedStatement.setString(1, firstName);
                preparedStatement.setString(2, lastName);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    user = getUser(resultSet);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return user;
        };

        return testConnection.doTransaction(transaction);
    }

    private List<User> getAllUsersFromDB() {

        Function<Connection, List<User>> transaction = connection->{
            List<User> users = new ArrayList<>();

            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(
                        "select * from users left join regions on users.region_id " +
                                "= regions.id");
                while (resultSet.next()) {
                    users.add(getUser(resultSet));
                }
                resultSet.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return users;
        };

        return testConnection.doTransaction(transaction);
    }

    private User getUser(ResultSet resultSet) throws SQLException {
        long   id            = resultSet.getLong("users.id");
        String userFirstName = resultSet.getString("first_name");
        String userLastName  = resultSet.getString("last_name");
        Long   regionId      = resultSet.getLong("regions.id");
        String regionName    = resultSet.getString("regions.name");
        Role   userRole      = Role.valueOf(resultSet.getString("role"));
        return new User(id, userFirstName, userLastName, new Region(regionId, regionName),
                        userRole);
    }

    @Nested
    @DisplayName ("Tests for the add method")
    class addMethodTests {


        @Test
        @DisplayName ("When user added into database, then database contains user")
        public void whenAddNewUserThenUserExistInDataBase() {
            //given
            Region region       = new Region(1, "TestRegion");
            User   expectedUser = new User(1, "UserName", "UserLastName", region, Role.ADMIN);

            //when
            regionRepository.add(region);
            userRepository.add(expectedUser);
            User actualUser = getUserFromDb(expectedUser.getFirstName(),
                                            expectedUser.getLastName());

            //then
            assertThat(actualUser)
                    .usingRecursiveComparison()
                    .ignoringFields("id", "region")
                    .isEqualTo(expectedUser);

        }

        @Test
        @DisplayName ("When add user, method return added user")
        public void addUserReturnAddedUser() {
            //given
            Region region       = new Region(1, "TestRegion");
            User   expectedUser = new User(1, "UserName", "UserLastName", region, Role.MODERATOR);

            //when
            regionRepository.add(region);
            User actualUser = userRepository.add(expectedUser);

            //then
            assertThat(actualUser).usingRecursiveComparison().ignoringFields("id").isEqualTo(
                    actualUser);

        }
    }

    @Nested
    @DisplayName ("Tests for the get method")
    class getMethodTest {

        @Test
        @DisplayName ("If user exist in DB then it will be returned")
        public void returnAddedUser() {
            //given
            Region region       = new Region(1, "TestRegion");
            User   expectedUser = new User(1, "TestName", "TestLastName", region, Role.ADMIN);
            regionRepository.add(region);
            userRepository.add(expectedUser);
            Long userId = getUserFromDb(expectedUser.getFirstName(),
                                        expectedUser.getLastName()).getId();

            //when
            User actualUser = userRepository.get(userId);

            //then
            assertThat(actualUser).usingRecursiveComparison().ignoringFields("id").isEqualTo(
                    expectedUser);
        }

        @Test
        @DisplayName ("When get user which not exist then throw exception")
        public void whenUserNotExistInDBReturnException() {
            //given

            //when
            Throwable throwable = catchThrowable(()->userRepository.get(1L));

            //then
            assertThat(throwable).isInstanceOf(IllegalArgumentException.class).hasMessageContaining(
                    "Illegal user id");
        }
    }

    @Nested
    @DisplayName ("Tests for the change method")
    class changeMethodTests {

        @Test
        @DisplayName ("When user changed then data base return changed user")
        public void whenUserChangedThenDBReturnChangedUser() {
            //given
            Region region             = new Region(1, "TestRegion");
            User   userBeforeChanging = new User(1, "TestName", "TestLastName", region, Role.USER);
            regionRepository.add(region);
            userRepository.add(userBeforeChanging);

            //when
            User expectedUser = getUserFromDb(userBeforeChanging.getFirstName(),
                                              userBeforeChanging.getLastName());
            expectedUser.changeUserRole("ADMIN");
            expectedUser.setLastName("ChangedLastName");
            userRepository.change(expectedUser);

            //then
            User actualUser = getUserFromDb(expectedUser.getFirstName(),
                                            expectedUser.getLastName());
            assertThat(actualUser).isEqualTo(expectedUser);
        }
    }

    @Nested
    @DisplayName ("Tests for the remove method")
    class removeMethodTests {

        @Test
        @DisplayName ("When user removed from DB then it not exist")
        public void whenRemoveUserThenUserIsNotExistInDB() {
            //given
            Region region = new Region(1, "TestRegion");
            regionRepository.add(region);
            Stream.of(new User(1, "TestName1", "TestLastName1", region, Role.MODERATOR),
                      new User(2, "TestName2", "TestLastName2", region, Role.USER),
                      new User(3, "TestName3", "TestLastName3", region, Role.USER)).forEach(
                    userRepository::add);

            //when
            User userFromDataBase = getUserFromDb("TestName1", "TestLastName1");
            userRepository.remove(userFromDataBase.getId());

            //then
            List<User> actualUserSet = getAllUsersFromDB();
            Assertions.assertAll(()->{
                assertThat(actualUserSet).extracting(User::getFirstName).doesNotContain(
                        "TestName1");
                assertThat(actualUserSet).extracting(User::getFirstName).containsOnly("TestName2",
                                                                                      "TestName3");
            });
        }
    }

    @Nested
    @DisplayName ("Tests for the getAll method")
    class getAllMethodTests {

        @Test
        @DisplayName ("When get all users from database then return list with all users")
        public void whenGetAllThenReturnListOfUsers() {
            //given
            Region region = new Region(1, "TestRegion");
            regionRepository.add(region);

            List<User> expectedListOfUser = List.of(
                    new User(1, "Name1", "LastName1", region, Role.USER),
                    new User(2, "Name2", "LastName2", region, Role.MODERATOR),
                    new User(3, "Name3", "LastName3", region, Role.ADMIN));
            expectedListOfUser.forEach(userRepository::add);

            //when
            List<User> actualUserList = userRepository.getAll();

            //then
            assertThat(actualUserList).usingRecursiveComparison().ignoringFields("id").isEqualTo(
                    expectedListOfUser);
        }

        @Test
        @DisplayName ("When database is empty, then return empty list")
        public void whenDBIsEmptyThenReturnEmptyList() {
            //when
            List<User> actualUserList = userRepository.getAll();

            //then
            assertThat(actualUserList).isEmpty();
        }
    }

    @Nested
    @DisplayName ("Tests for the removeAll method")
    class removeAllMethodsTests {

        @Test
        @DisplayName ("When remove all then database return empty list")
        public void whenRemoveAllThenDataBaseIsEmpty() {
            //given
            Region region = new Region(1, "TestRegion");
            regionRepository.add(region);

            List<User> expectedListOfUser = List.of(
                    new User(1, "Name1", "LastName1", region, Role.USER),
                    new User(2, "Name2", "LastName2", region, Role.MODERATOR),
                    new User(3, "Name3", "LastName3", region, Role.ADMIN));
            expectedListOfUser.forEach(userRepository::add);

            //when
            userRepository.removeAll();
            List<User> actualUserList = getAllUsersFromDB();

            //then
            assertThat(actualUserList).isEmpty();
        }
    }

    @Nested
    @DisplayName ("Tests for the isExist method")
    class isExistMethodTests {

        @Test
        @DisplayName ("When user exist in database, then return true")
        public void whenUserExistInDBThenReturnTrue() {
            //given
            Region region = new Region(1, "TestRegion");
            User   user   = new User(1, "TestName", "TestLastName", region, Role.ADMIN);
            regionRepository.add(region);
            userRepository.add(user);

            //when
            User userFromDB = getUserFromDb(user.getFirstName(), user.getLastName());
            boolean actualStatus = userRepository.isContains(userFromDB.getId());

            //then
            assertThat(actualStatus).isTrue();
        }

        @Test
        @DisplayName("When user is not exist in database, then return false")
        public void whenUserIsNotExistReturnFalse() {
            //when
            boolean actualStatus = userRepository.isContains(125l);

            //then
            assertThat(actualStatus).isFalse();
        }

    }


}