package com.valentinNikolaev.jdbcCrud.controller;

import com.valentinNikolaev.jdbcCrud.models.Post;
import com.valentinNikolaev.jdbcCrud.models.User;
import com.valentinNikolaev.jdbcCrud.repository.PostRepository;

import java.util.List;
import java.util.Optional;

public class PostControllerImpl implements PostController {
    private PostRepository postRepository;
    private UserController userController;

    public PostControllerImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public PostControllerImpl(PostRepository postRepository, UserController userController) {
        this.postRepository = postRepository;
        this.userController = userController;
    }

    public void setUserController(UserController userController) {
        this.userController = userController;
    }

    @Override
    public Post addPost(String userId, String content) {
        Optional<User> user = this.userController.getUserById(userId);
        if (user.isPresent()) {
            long postId = this.getLastPostId() + 1;
            return this.postRepository.add(new Post(postId, user.get().getId(), content));
        } else {
            throw new IllegalArgumentException("The user with id: " + userId + " is not exists.");
        }
    }

    @Override
    public Optional<Post> getPost(String postId) {
        long id = Long.parseLong(postId);
        Optional<Post> post = this.postRepository.isContains(id)
                              ? Optional.of(this.postRepository.get(id))
                              : Optional.empty();

        return post;
    }

    @Override
    public List<Post> getAllPostsList() {
        return this.postRepository.getAll();
    }

    @Override
    public List<Post> getPostsByUserId(String userId) {
        long id = Long.parseLong(userId);
        return this.postRepository.getPostsByUserId(id);
    }

    @Override
    public boolean changePost(String postId, String newContent) {
        long id = Long.parseLong(postId);
        if (this.postRepository.isContains(id)) {
            Post post = this.postRepository.get(id);
            post.setContent(newContent);
            this.postRepository.change(post);
        }
        return this.postRepository.get(id).getContent().equals(newContent);
    }

    @Override
    public boolean removePost(String postId) {
        long id = Long.parseLong(postId);
        return this.postRepository.remove(id);
    }

    @Override
    public boolean removeAllPostByUser(String userId) {
        long id = Long.parseLong(userId);
        return this.postRepository.removePostsByUserId(id);
    }

    @Override
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
