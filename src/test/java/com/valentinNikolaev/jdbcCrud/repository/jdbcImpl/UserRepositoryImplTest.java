package com.valentinNikolaev.jdbcCrud.repository.jdbcImpl;

import com.valentinNikolaev.jdbcCrud.models.Post;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

@TestInstance (TestInstance.Lifecycle.PER_CLASS)
@DisplayName ("Tests for user DAO class based on JDBC")
class UserRepositoryImplTest {

    private TestConnection testConnection = new TestConnection();
    private PostRepository postRepository = new PostRepositoryImpl(
            testConnection.getConnectionFactory());
    private UserRepository userRepository = new UserRepositoryImpl(
            testConnection.getConnectionFactory(), postRepository);

    @BeforeAll
    public void cleanDataBaseBeforeAllTest() {
        testConnection.cleanDataBase();
    }

    @AfterEach
    public void cleanDataBaseAfterTests() {
        testConnection.cleanDataBase();
    }

    @Nested
    @DisplayName ("Tests for the add method")
    class addMethodTests {


        @Test
        @DisplayName ("When user added into database, then database contains user")
        public void whenAddNewUserThenUserExistInDataBase() {
            //given
            Region region = testConnection.addRegionIntoDB("TestRegion");
            User expectedUser = new User(1, "UserName", "UserLastName", region, Role.ADMIN);

            //when
            userRepository.add(expectedUser);
            User actualUser = testConnection.getUserFromDb(expectedUser.getFirstName(),
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
            Region region = testConnection.getRegionFromDB("TestRegion");
            User expectedUser = new User(1, "UserName", "UserLastName", region, Role.MODERATOR);

            //when
            User actualUser = userRepository.add(expectedUser);

            //then
            assertThat(actualUser)
                    .usingRecursiveComparison()
                    .ignoringFields("id")
                    .isEqualTo(actualUser);

        }
    }

    @Nested
    @DisplayName ("Tests for the get method")
    class getMethodTest {

        @Test
        @DisplayName ("If user exist in DB then it will be returned")
        public void returnAddedUser() {
            //given
            Region region = testConnection.addRegionIntoDB("TestRegion");
            User expectedUser = testConnection.addUserIntoDB("TestName", "TestLastName",
                                                             region.getId(), Role.ADMIN);

            //when
            User actualUser = userRepository.get(expectedUser.getId());

            //then
            assertThat(actualUser).isEqualTo(expectedUser);
        }

        @Test
        @DisplayName ("When get user which not exist then throw exception")
        public void whenUserNotExistInDBReturnException() {
            //given

            //when
            Throwable throwable = catchThrowable(()->userRepository.get(1L));

            //then
            assertThat(throwable)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Illegal user id");
        }

        @Test
        @DisplayName ("When user has posts then return user with list of all posts")
        public void whenUserHasPostsThenUserReturnWithAllPosts() {
            //given
            Region region = testConnection.addRegionIntoDB("TestRegion");
            User testUser = testConnection.addUserIntoDB("TestName", "TestLastName", region.getId(),
                                                         Role.ADMIN);
            List<Post> expectedUserPosts = List
                    .of(new Post(1, testUser.getId(), "TestContent1"),
                        new Post(2, testUser.getId(), "Test content2"),
                        new Post(3, testUser.getId(), "Test Content3"))
                    .stream()
                    .map(testConnection::addPostIntoDB)
                    .collect(Collectors.toList());

            //when
            User actualUser = userRepository.get(testUser.getId());
            List<Post> actualUserPosts = actualUser.getPosts();

            //then
            assertThat(actualUserPosts)
                    .usingRecursiveComparison()
                    .ignoringCollectionOrder()
                    .isEqualTo(expectedUserPosts);
        }
    }

    @Nested
    @DisplayName ("Tests for the change method")
    class changeMethodTests {

        @Test
        @DisplayName ("When user changed then data base return changed user")
        public void whenUserChangedThenDBReturnChangedUser() {
            //given
            Region region = testConnection.addRegionIntoDB("TestRegion");
            User userBeforeChanging = testConnection.addUserIntoDB("UserName", "UserLastName",
                                                                   region.getId(), Role.ADMIN);

            //when
            User expectedUser = userBeforeChanging
                    .changeUserRole("ADMIN")
                    .setLastName("ChangedLastName");
            userRepository.change(expectedUser);

            //then
            User actualUser = testConnection.getUserFromDb(expectedUser.getFirstName(),
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
            Region region = testConnection.addRegionIntoDB("TestRegion");
            Stream<User> userStream = Stream.of(
                    new User(1, "TestName1", "TestLastName1", region, Role.MODERATOR),
                    new User(2, "TestName2", "TestLastName2", region, Role.USER),
                    new User(3, "TestName3", "TestLastName3", region, Role.USER));

            userStream.forEach(testConnection::addUserIntoDB);

            //when
            User userFromDataBase = testConnection.getUserFromDb("TestName1", "TestLastName1");
            userRepository.remove(userFromDataBase.getId());

            //then
            List<User> actualUserSet = testConnection.getAllUsersFromDB();
            Assertions.assertAll(()->{
                assertThat(actualUserSet)
                        .extracting(User::getFirstName)
                        .doesNotContain("TestName1");
                assertThat(actualUserSet)
                        .extracting(User::getFirstName)
                        .containsOnly("TestName2", "TestName3");
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
            Region region = testConnection.addRegionIntoDB("TestRegion");

            List<User> testUsers = List.of(new User(1, "Name1", "LastName1", region, Role.USER),
                                           new User(2, "Name2", "LastName2", region,
                                                    Role.MODERATOR),
                                           new User(3, "Name3", "LastName3", region, Role.ADMIN));

            List<User> expectedUsersList = testUsers
                    .stream()
                    .map(testConnection::addUserIntoDB)
                    .collect(Collectors.toList());

            //when
            List<User> actualUserList = userRepository.getAll();

            //then
            assertThat(actualUserList)
                    .usingRecursiveComparison()
                    .ignoringFields("id")
                    .isEqualTo(expectedUsersList);
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
            Region region = testConnection.addRegionIntoDB("TestRegion");

            List<User> testUsers = List.of(new User(1, "Name1", "LastName1", region, Role.USER),
                                           new User(2, "Name2", "LastName2", region,
                                                    Role.MODERATOR),
                                           new User(3, "Name3", "LastName3", region, Role.ADMIN));

            testUsers.forEach(testConnection::addUserIntoDB);


            //when
            userRepository.removeAll();
            List<User> actualUserList = testConnection.getAllUsersFromDB();

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
            Region region = testConnection.addRegionIntoDB("TestRegion");
            User expectedUser = testConnection.addUserIntoDB("TestName", "TestLastName",
                                                             region.getId(), Role.ADMIN);

            //when
            boolean actualStatus = userRepository.isContains(expectedUser.getId());

            //then
            assertThat(actualStatus).isTrue();
        }

        @Test
        @DisplayName ("When user is not exist in database, then return false")
        public void whenUserIsNotExistReturnFalse() {
            //when
            boolean actualStatus = userRepository.isContains(125l);

            //then
            assertThat(actualStatus).isFalse();
        }

    }
}