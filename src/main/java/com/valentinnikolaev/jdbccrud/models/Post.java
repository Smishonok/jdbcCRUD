package com.valentinnikolaev.jdbccrud.models;

import java.time.Clock;
import java.time.LocalDateTime;

public class Post {
    private Long id;
    private Long userId;
    private String content;
    private LocalDateTime created;
    private LocalDateTime updated;
    private Clock clock;

    public Post(Long id, Long userId, String content, LocalDateTime created,
                LocalDateTime updated) {
        this.id      = id;
        this.userId  = userId;
        this.content = content;
        this.created = created;
        this.updated = updated;
        this.clock = Clock.systemUTC();
    }

    public Post(Long id, Long userId, String content, Clock clock) {
        this.id      = id;
        this.userId  = userId;
        this.content = content;
        this.clock = clock;
        this.created = LocalDateTime.now(clock);
        this.updated = created;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getDateOfCreation() {
        return created;
    }

    public LocalDateTime getDateOfLastUpdate() {
        return updated;
    }

    public void setContent(String content) {
        this.content = content;
        this.updated = LocalDateTime.now(clock);
    }

    @Override
    public int hashCode() {
        int hash = this.content.hashCode() + userId.intValue() + created.hashCode() + updated.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        if (this.hashCode() != obj.hashCode()) {
            return false;
        }

        Post comparingObj = (Post) obj;
        return this.content.equals(comparingObj.content) && this.userId == comparingObj.getUserId();
    }

    public boolean equalsContent(Post post) {
        return this.content.equals(post.getContent());
    }

    @Override
    public String toString() {
        return "Post{" + "id=" + id + ", userId=" + userId + ", content='" + content + '\'' +
               ", created=" + created + ", updated=" + updated + '}';
    }
}
