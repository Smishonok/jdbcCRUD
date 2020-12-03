package com.valentinnikolaev.jdbccrud.repository.jdbc;

import com.valentinnikolaev.jdbccrud.models.Post;
import com.valentinnikolaev.jdbccrud.models.Region;
import com.valentinnikolaev.jdbccrud.models.Role;
import com.valentinnikolaev.jdbccrud.models.User;
import com.valentinnikolaev.jdbccrud.repository.PostRepository;
import com.valentinnikolaev.jdbccrud.utils.TestConnection;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@TestInstance (TestInstance.Lifecycle.PER_CLASS)
@DisplayName ("Tests for post DAO class based on JDBC")
class JdbcJsonPostRepositoryImplTest {

    private TestConnection testConnection = new TestConnection();
    private PostRepository postRepository = new JdbcPostRepositoryImpl(
            testConnection.getConnectionFactory());


    @BeforeAll
    public void clearDatabaseBeforeTests() {
        testConnection.cleanDataBase();
    }

    @AfterEach
    public void cleatBaseAfterTest() {
        testConnection.cleanDataBase();
    }


    @Nested
    @DisplayName ("Tests for the add method")
    class TestsForAddMethod {

        @Test
        @DisplayName ("When add post then post exist in database")
        public void whenAddPostThenPostExistInDB() {
            //given
            Region region = testConnection.addRegionIntoDB("testRegion");
            User user = testConnection.addUserIntoDB("UserName", "UserLastName", region.getId(),
                                                     Role.ADMIN);
            LocalDateTime timeOfCreating = LocalDateTime.of(2020, 11, 19, 16, 53, 0, 0);
            LocalDateTime timeOfUpdating = LocalDateTime.of(2020, 11, 19, 16, 53, 0, 0);
            Post expectedPost = new Post(1l, user.getId(), "Test content", timeOfCreating,
                                         timeOfUpdating);

            //when
            postRepository.add(expectedPost);

            //then
            Post actualPost = testConnection.getPostFromDB(user, expectedPost.getContent(),
                                                           expectedPost.getDateOfCreation());
            assertThat(actualPost)
                    .usingRecursiveComparison()
                    .ignoringFields("id")
                    .isEqualTo(expectedPost);
        }

        @Test
        @DisplayName ("When add post into repository then return it")
        public void whenAddPostThenReturnPostFromDB() {
            //given
            Region region = testConnection.addRegionIntoDB("TestRegion");
            User user = testConnection.addUserIntoDB("UserName", "LastName", region.getId(),
                                                     Role.ADMIN);
            LocalDateTime timeOfCreating = LocalDateTime.of(2020, 11, 19, 16, 53, 0, 0);
            LocalDateTime timeOfUpdating = LocalDateTime.of(2020, 11, 19, 16, 53, 0, 0);
            Post expectedPost = new Post(1l, user.getId(), "TestContent", timeOfCreating,
                                         timeOfUpdating);

            //when
            Post actualPost = postRepository.add(expectedPost);

            //then
            assertThat(actualPost)
                    .usingRecursiveComparison()
                    .ignoringFields("id")
                    .isEqualTo(expectedPost);
        }
    }


    @Nested
    @DisplayName ("Tests for the get method")
    class TestsForGetMethods {

        @Test
        @DisplayName ("When get post which exist in database then return post")
        public void whenGetPostThenReturnPost() {
            //given
            Region region = testConnection.addRegionIntoDB("TestRegion");
            User user = testConnection.addUserIntoDB("UserName", "UserLastName", region.getId(),
                                                     Role.ADMIN);
            LocalDateTime timeOfCreating = LocalDateTime.of(2020, 11, 19, 16, 53, 0, 0);
            LocalDateTime timeOfUpdating = LocalDateTime.of(2020, 11, 19, 16, 53, 0, 0);

            Post expectedPost = testConnection.addPostIntoDB("Test content", user.getId(),
                                                             timeOfCreating.toEpochSecond(
                                                                     ZoneOffset.UTC),
                                                             timeOfUpdating.toEpochSecond(
                                                                     ZoneOffset.UTC));

            //when
            Post actualPost = postRepository.get(expectedPost.getId());

            //then
            assertThat(actualPost).isEqualTo(expectedPost);
        }

        @Test
        @DisplayName ("When get post which is not exist then trow exception")
        public void whenGetPostWhichNotExistThenThrowException() {
            //when
            Throwable throwable = catchThrowable(()->postRepository.get(145l));

            //then
            assertThat(throwable)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Illegal post id");
        }
    }


    @Nested
    @DisplayName ("Tests for the getByUserId method")
    class TestForGetByUserIdMethod {

        @Test
        @DisplayName ("When get posts by user id and posts exist then return list of posts")
        public void whenGetByUserAndPostWithUserIdExistsThenReturnListOfPosts() {
            //given
            Region region = testConnection.addRegionIntoDB("Test region");
            User user = testConnection.addUserIntoDB("UserName", "UserLastName", region.getId(),
                                                     Role.ADMIN);
            List<Post> expectedUserPosts = List
                    .of(new Post(1, user.getId(), "Content 1"),
                        new Post(2, user.getId(), "Content2"),
                        new Post(3, user.getId(), "Content3"))
                    .stream()
                    .map(testConnection::addPostIntoDB)
                    .collect(Collectors.toList());

            //when
            List<Post> actualUserPosts = postRepository.getPostsByUserId(user.getId());

            //then
            assertThat(actualUserPosts)
                    .usingRecursiveComparison()
                    .ignoringCollectionOrder()
                    .isEqualTo(expectedUserPosts);
        }

        @Test
        @DisplayName ("When get by user name and posts is not exist then return empty list")
        public void whenGetByUserIdAndPostsNotExistThenReturnEmptyList() {
            //when
            List<Post> actualUserPosts = postRepository.getPostsByUserId(145l);

            //then
            assertThat(actualUserPosts).isEmpty();
        }
    }

    @Nested
    @DisplayName ("Tests for removeByUserId method")
    class TestsForRemoveByUserIdMethod {


        @Test
        @DisplayName (
                "When remove posts by user id then database contains posts excepting user`s " +
                "posts")
        public void whenRemovePostByUserIdThenDBContainsPostsExceptUser() {
            //given
            Region region = testConnection.addRegionIntoDB("Test region");
            User user1 = testConnection.addUserIntoDB("UserName", "UserLastName", region.getId(),
                                                      Role.ADMIN);
            User user2 = testConnection.addUserIntoDB("User2Name", "User2LastName", region.getId(),
                                                      Role.MODERATOR);
            List<Post> expectedUserPosts = List
                    .of(new Post(1, user2.getId(), "Content 1"),
                        new Post(2, user1.getId(), "Content2"),
                        new Post(3, user2.getId(), "Content3"))
                    .stream()
                    .map(testConnection::addPostIntoDB)
                    .collect(Collectors.toList());

            //when
            postRepository.removePostsByUserId(user1.getId());

            //then
            List<Post> actualPosts = testConnection.getAllPosts();
            assertThat(actualPosts)
                    .extracting("content")
                    .containsExactlyInAnyOrder("Content 1", "Content3");
        }
    }


    @Nested
    @DisplayName ("Tests for the remove method")
    class TestsForRemoveMethod {

        @Test
        @DisplayName ("When remove post then database not contains it")
        public void whenRemovePostThenDBNotContainsIt() {
            //given
            Region region = testConnection.addRegionIntoDB("Test region");
            User user = testConnection.addUserIntoDB("UserName", "UserLastName", region.getId(),
                                                     Role.ADMIN);
            List<Post> expectedUserPosts = List
                    .of(new Post(1, user.getId(), "Content 1"),
                        new Post(2, user.getId(), "Content2"),
                        new Post(3, user.getId(), "Content3"))
                    .stream()
                    .map(testConnection::addPostIntoDB)
                    .collect(Collectors.toList());

            //when
            boolean removingStatus = postRepository.remove(1l);

            //then
            List<Post> actualPosts = testConnection.getAllPosts();
            assertThat(removingStatus).isEqualTo(true);
            assertThat(actualPosts)
                    .extracting("content")
                    .containsExactlyInAnyOrder("Content2", "Content3");
        }
    }

    @Nested
    @DisplayName ("Tests for the getAll method")
    class getAllMethodTests {

        @Test
        @DisplayName ("When get all posts from database then return list with all posts")
        public void whenGetAllThenReturnListOfAllPosts() {
            //given
            Region region = testConnection.addRegionIntoDB("Test region");
            User user = testConnection.addUserIntoDB("UserName", "UserLastName", region.getId(),
                                                     Role.ADMIN);
            List<Post> expectedPostsList = List
                    .of(new Post(1, user.getId(), "Content 1"),
                        new Post(2, user.getId(), "Content2"),
                        new Post(3, user.getId(), "Content3"))
                    .stream()
                    .map(testConnection::addPostIntoDB)
                    .collect(Collectors.toList());

            //when
            List<Post> actualPostList = postRepository.getAll();

            //then
            assertThat(actualPostList)
                    .usingRecursiveComparison()
                    .ignoringCollectionOrder()
                    .isEqualTo(expectedPostsList);
        }

        @Test
        @DisplayName ("When database is empty, then return empty list")
        public void whenDBIsEmptyThenReturnEmptyList() {
            //when
            List<Post> actualUserList = postRepository.getAll();

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
            Region region = testConnection.addRegionIntoDB("Test region");
            User user = testConnection.addUserIntoDB("UserName", "UserLastName", region.getId(),
                                                     Role.ADMIN);
            List<Post> expectedPostsList = List
                    .of(new Post(1, user.getId(), "Content 1"),
                        new Post(2, user.getId(), "Content2"),
                        new Post(3, user.getId(), "Content3"))
                    .stream()
                    .map(testConnection::addPostIntoDB)
                    .collect(Collectors.toList());

            //when
            postRepository.removeAll();
            List<Post> actualPostsList = testConnection.getAllPosts();

            //then
            assertThat(actualPostsList).isEmpty();
        }
    }
}