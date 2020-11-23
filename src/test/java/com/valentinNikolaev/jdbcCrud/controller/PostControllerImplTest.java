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
                    .when(postRepositoryStub.add()
                    .thenReturn(expectedPost);

            //when
            Post actualPost = postController.addPost("1", expectedPost.getContent());

            //then
            assertThat(actualPost).isEqualTo(expectedPost);
        }

    }


}