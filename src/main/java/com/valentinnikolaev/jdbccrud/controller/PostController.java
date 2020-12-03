package com.valentinnikolaev.jdbccrud.controller;

import com.valentinnikolaev.jdbccrud.models.Post;
import com.valentinnikolaev.jdbccrud.models.User;
import com.valentinnikolaev.jdbccrud.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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
        this.clock = Clock.systemUTC();
    }

    public PostController(PostRepository postRepository, UserController userController,
                          Clock clock) {
        this.postRepository = postRepository;
        this.userController = userController;
        this.clock          = clock;
    }

    public Post addPost(String userId, String content) {
        Optional<User> user = this.userController.getUserById(userId);
        if (user.isPresent()) {
            long postId = this.getLastPostId() + 1;
            return this.postRepository.add(new Post(postId, user.get().getId(), content, clock));
        } else {
            throw new IllegalArgumentException("The user with id: " + userId + " is not exists.");
        }
    }

    public Optional<Post> getPost(String postId) {
        long id = Long.parseLong(postId);
        Optional<Post> post = this.postRepository.isContains(id)
                              ? Optional.of(this.postRepository.get(id))
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
        if (this.postRepository.isContains(id)) {
            Post post = this.postRepository.get(id);
            post.setContent(newContent);
            this.postRepository.change(post);
        }
        return this.postRepository.get(id).getContent().equals(newContent);
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
