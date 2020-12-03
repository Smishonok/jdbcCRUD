package com.valentinnikolaev.jdbccrud.repository;

import com.valentinnikolaev.jdbccrud.models.Post;

import java.util.List;

public interface PostRepository extends GenericRepository<Post,Long> {

    List<Post> getPostsByUserId(Long userId);

    boolean removePostsByUserId(Long userId);


}
