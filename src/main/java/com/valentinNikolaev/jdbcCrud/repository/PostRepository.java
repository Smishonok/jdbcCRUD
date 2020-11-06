package com.valentinNikolaev.jdbcCrud.repository;

import com.valentinNikolaev.jdbcCrud.models.Post;

import java.util.List;

public interface PostRepository extends GenericRepository<Post,Long> {

    List<Post> getPostsByUserId(Long userId);

    boolean removePostsByUserId(Long userId);


}
