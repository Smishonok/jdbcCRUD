package com.valentinNikolaev.jdbcCrud.controller;

import com.valentinNikolaev.jdbcCrud.models.Post;
import com.valentinNikolaev.jdbcCrud.models.Region;
import com.valentinNikolaev.jdbcCrud.models.Role;
import com.valentinNikolaev.jdbcCrud.models.User;
import com.valentinNikolaev.jdbcCrud.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@TestInstance (TestInstance.Lifecycle.PER_CLASS)
class PostControllerImplTest {

    private PostRepository postRepositoryStub = Mockito.mock(PostRepository.class);
    private UserController userControllerStub = Mockito.mock(UserController.class);
    private PostController postController     = new PostControllerImpl(postRepositoryStub,
                                                                       userControllerStub);

    @Nested
    class TestsForAddPostMethod {


        @Test
        @DisplayName (
                "When add post for user which not exist then throw illegal argument exception")
        public void whenAddPostForUserWhichNotExistThenThrowIllegalArgException() {
            //given
            Mockito.when(userControllerStub.getUserById("1")).thenReturn(Optional.empty());

            //when
            Throwable throwable = catchThrowable(()->postController.addPost("1", "TestContent"));

            //then
            assertThat(throwable).isInstanceOf(IllegalArgumentException.class).hasMessageContaining(
                    "user with id");
        }

        @Test
        @DisplayName ("When add post for user which exist then return added post")
        public void whenAddPostForUserWhichExistThenReturnAddedPost() {
            //given
            User user = new User(1l, "UserName", "UserLastName", new Region(1l, "TestRegion"),
                                 Role.ADMIN);

            Mockito.when(userControllerStub.getUserById("1")).thenReturn(Optional.of(user));
            List<Post> posts = new ArrayList<>();
            posts.add(new Post(1, 1, "TestPost1"));
            posts.add(new Post(2, 1, "TestPost2"));
            Mockito.when(postRepositoryStub.getAll()).thenReturn(posts);

            Post expectedPost = new Post(3, 1, "Expected post");
            Mockito
                    .when(postRepositoryStub.add(Mockito.any(Post.class)))
                    .thenReturn(expectedPost);

            //when
            Post actualPost = postController.addPost("1", expectedPost.getContent());

            //then
            assertThat(actualPost).isEqualTo(expectedPost);
        }
    }


    @Nested
    class TestsForGetPostMethod{

        @Test
        @DisplayName("When get post which exist in database then return post")
        public void whenGetPostWhichExistInDbThenReturnPost() {
            //given
            Post expectedPost = new Post(1l, 1, "Test content");
            Mockito.when(postRepositoryStub.isContains(1l)).thenReturn(true);
            Mockito.when(postRepositoryStub.get(1l)).thenReturn(expectedPost);

            //when
            Post actualPost = postController.getPost("1").get();

            //then
            assertThat(actualPost).isEqualTo(expectedPost);
        }

        @Test
        @DisplayName("When get post which is not exist in database then return empty optional")
        public void whenGetPostWhichNotExistInDbThenReturnEmptyOptional() {
            //given
            Optional<Post> expectedOptionalValue = Optional.empty();

            //when
            Optional<Post> actualValue = postController.getPost("1");

            //then
            assertThat(actualValue).isEqualTo(expectedOptionalValue);
        }
    }

    @Nested
    class TestsForGetAllMethod{

        @Test
        @DisplayName("When getAll posts from repository then return all posts")
        public void whenGetAllThenReturnAllPostFromRepository(){
            //given
            List<Post> expectedPosts = List.of(new Post(1l, 1l, "TestPost1"),
                                               new Post(2l, 1l, "TestPost2"),
                                               new Post(3l, 2l, "Post from another user"));
            Mockito.when(postRepositoryStub.getAll()).thenReturn(expectedPosts);

            //when
            List<Post> actualPosts = postController.getAllPostsList();

            //then
            assertThat(actualPosts).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(
                    expectedPosts);
        }
    }

    @Nested
    class TestsForGetPostByUserIdMethod{

        @Test
        @DisplayName("When get post by user id then return user posts only")
        public void whenGetPostByUserIdThenReturnUserPostOnly() {
            //given
            List<Post> expectedPosts = List.of(new Post(1l, 1l, "TestPost1"),
                                               new Post(2l, 1l, "TestPost2"),
                                               new Post(3l, 1l, "One more post from user"));
            Mockito.when(postRepositoryStub.getPostsByUserId(1l)).thenReturn(expectedPosts);

            //when
            List<Post> actualPosts = postController.getPostsByUserId("1");

            //then
            assertThat(actualPosts)
                    .usingRecursiveComparison()
                    .ignoringCollectionOrder()
                    .isEqualTo(expectedPosts);
        }

        @Test
        @DisplayName("When get list of post by user id and posts are not exist then return empty " +
                     "list")
        public void whenGetPostByUserIdAndPostsNotExistThenReturnEmptyList() {
            //given
            Mockito.when(postRepositoryStub.getPostsByUserId(1l)).thenReturn(new ArrayList<>());

            //when
            List<Post> actualPostList = postController.getPostsByUserId("1");

            //then
            assertThat(actualPostList).isEmpty();
        }
    }

    @Nested
    class TestsForChangePostMethod {

        @Test
        @DisplayName("When change post content then return changed post")
        public void whenChangePostThenReturnChangedPost() {
            //given
            Post postBeforeChanging = new Post(1l, 1l, "Test post");
            Mockito.when(postRepositoryStub.isContains(1l)).thenReturn(true);
            Mockito.when(postRepositoryStub.get(1l)).thenReturn(postBeforeChanging);
            postBeforeChanging.setContent("Changed test post");
            Post expectedPost = postBeforeChanging;
            Mockito.when(postRepositoryStub.change(expectedPost)).thenReturn(expectedPost);

        }
    }
}
