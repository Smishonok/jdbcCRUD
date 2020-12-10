package com.valentinnikolaev.jdbccrud.controller;

import com.valentinnikolaev.jdbccrud.models.Post;
import com.valentinnikolaev.jdbccrud.models.User;
import com.valentinnikolaev.jdbccrud.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.swing.text.html.Option;
import java.time.Clock;
import java.util.List;
import java.util.Optional;

@Component
@Scope ("singleton")
public class PostController {
    private PostRepository postRepository;
    private UserController userController;
    private Clock clock;

    public PostController(@Autowired PostRepository postRepository,
                          @Autowired UserController userController) {
        this.postRepository = postRepository;
        this.userController = userController;
        this.clock          = Clock.systemUTC();
    }

    public PostController(PostRepository postRepository, UserController userController,
                          Clock clock) {
        this.postRepository = postRepository;
        this.userController = userController;
        this.clock          = clock;
    }

    public void addPost(String userId, String content) {
        Optional<User> user = this.userController.getUserById(userId);

        Optional<Post> post = Optional.empty();
        if (user.isPresent()) {
            long postId = this.getLastPostId() + 1;
            post = postRepository.add((new Post(postId, user.get().getId(), content, clock)));
        }

        if (post.isEmpty()) {
            System.out.println("Error! Post was`t added into repository.");
        }
    }

    public Optional<Post> getPost(String postId) {
        long id = Long.parseLong(postId);
        Optional<Post> post = this.postRepository.isContains(id)
                              ? this.postRepository.get(id)
                              : Optional.empty();

        return post;
    }

    public List<Post> getAllPostsList() {
        return this.postRepository.getAll();
    }

    public List<Post> getPostsByUserId(String userId) {
        long id = Long.parseLong(userId);
        return this.postRepository.getPostsByUserId(id);
    }

    public boolean changePost(String postId, String newContent) {
        long id = Long.parseLong(postId);
        Optional<Post> postOptional = postRepository.get(id);

        Optional<Post> postOptionalFromDb = Optional.empty();
        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            post.setContent(newContent);
            postOptionalFromDb = postRepository.change(post);
        } else {
            System.out.printf("\nError: post with id %1$d was`t found in database\n", id);
            return false;
        }

        if (postOptionalFromDb.isPresent() && ! postOptional.get().equals(postOptionalFromDb.get())) {
            System.out.println("Post changing is completed");
            return true;
        } else {
            System.out.println("Post changing is not completed");
            return false;
        }
    }

    public boolean removePost(String postId) {
        long id = Long.parseLong(postId);
        return this.postRepository.remove(id);
    }

    public boolean removeAllPostByUser(String userId) {
        long id = Long.parseLong(userId);
        return this.postRepository.removePostsByUserId(id);
    }

    public boolean removeAllPosts() {
        return this.postRepository.removeAll();
    }

    private long getLastPostId() {
        Optional<Long> maxPostId = getAllPostsList().stream().map(Post::getId).max(Long::compareTo);
        return maxPostId.isPresent()
               ? maxPostId.get()
               : 0;
    }
}
