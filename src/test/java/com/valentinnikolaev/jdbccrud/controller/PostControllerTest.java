package com.valentinnikolaev.jdbccrud.controller;

import com.valentinnikolaev.jdbccrud.models.Post;
import com.valentinnikolaev.jdbccrud.repository.PostRepository;
import com.valentinnikolaev.jdbccrud.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@TestInstance (TestInstance.Lifecycle.PER_CLASS)
class PostControllerTest {

    private PostRepository postRepositoryStub = Mockito.mock(PostRepository.class);
    private UserRepository userRepository = Mockito.mock(UserRepository.class);
    private PostController postController     = new PostController(postRepositoryStub,
                                                                   userRepository);


    @Nested
    class TestsForGetPostMethod {

        @Test
        @DisplayName ("When get post which exist in database then return post")
        public void whenGetPostWhichExistInDbThenReturnPost() {
            Clock clock = Clock.fixed(Instant.parse("2020-12-14T10:15:30.00Z"), ZoneOffset.UTC);
            Post expectedPost = new Post(1L, 1L, "Test content",clock);
            Mockito.when(postRepositoryStub.isContains(1L)).thenReturn(true);
            Mockito.when(postRepositoryStub.get(1L)).thenReturn(Optional.of(expectedPost));
            Post actualPost = postController.getPost("1").get();
            assertThat(actualPost).isEqualTo(expectedPost);
        }

        @Test
        @DisplayName ("When get post which is not exist in database then return empty optional")
        public void whenGetPostWhichNotExistInDbThenReturnEmptyOptional() {
            Optional<Post> expectedOptionalValue = Optional.empty();
            Mockito.when(postRepositoryStub.isContains(1L)).thenReturn(false);
            Optional<Post> actualValue = postController.getPost("1");
            assertThat(actualValue).isEqualTo(expectedOptionalValue);
        }
    }

    @Nested
    class TestsForGetAllMethod {

        @Test
        @DisplayName ("When getAll posts from repository then return all posts")
        public void whenGetAllThenReturnAllPostFromRepository() {
            Clock clock = Clock.fixed(Instant.parse("2020-12-14T10:15:30.00Z"), ZoneOffset.UTC);
            List<Post> expectedPosts = List.of(new Post(1L, 1L, "TestPost1",clock),
                                               new Post(2L, 1L, "TestPost2",clock),
                                               new Post(3L, 2L, "Post from another user",clock));
            Mockito.when(postRepositoryStub.getAll()).thenReturn(expectedPosts);
            List<Post> actualPosts = postController.getAllPostsList();
            assertThat(actualPosts).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(
                    expectedPosts);
        }
    }

    @Nested
    class TestsForGetPostByUserIdMethod {

        @Test
        @DisplayName ("When get post by user id then return user posts only")
        public void whenGetPostByUserIdThenReturnUserPostOnly() {
            Clock clock = Clock.fixed(Instant.parse("2020-12-14T10:15:30.00Z"), ZoneOffset.UTC);
            List<Post> expectedPosts = List.of(new Post(1L, 1L, "TestPost1",clock),
                                               new Post(2L, 1L, "TestPost2",clock),
                                               new Post(3L, 1L, "One more post from user", clock));
            Mockito.when(postRepositoryStub.getPostsByUserId(1L)).thenReturn(expectedPosts);
            List<Post> actualPosts = postController.getPostsByUserId("1");
            assertThat(actualPosts).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(
                    expectedPosts);
        }

        @Test
        @DisplayName (
                "When get list of post by user id and posts are not exist then return empty " +
                        "list")
        public void whenGetPostByUserIdAndPostsNotExistThenReturnEmptyList() {
            Mockito.when(postRepositoryStub.getPostsByUserId(1L)).thenReturn(new ArrayList<>());
            List<Post> actualPostList = postController.getPostsByUserId("1");
            assertThat(actualPostList).isEmpty();
        }
    }

    @Nested
    class TestsForChangePostMethod {

        @Test
        @DisplayName ("When change post content then return changed post")
        public void whenChangePostThenReturnChangedPost() {
            Clock clock = Clock.fixed(Instant.parse("2020-12-14T10:15:30.00Z"), ZoneOffset.UTC);
            Post postBeforeChanging = new Post(1L, 1L, "Test post", clock);
            Mockito.when(postRepositoryStub.isContains(1L)).thenReturn(true);
            Mockito.when(postRepositoryStub.get(1L)).thenReturn(Optional.of(postBeforeChanging));
            postBeforeChanging.setContent("Changed test post");
            Post expectedPost = postBeforeChanging;
            Mockito.when(postRepositoryStub.change(expectedPost)).thenReturn(
                    Optional.of(expectedPost));
        }
    }
}
