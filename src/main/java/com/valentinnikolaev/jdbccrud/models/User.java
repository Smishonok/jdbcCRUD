package com.valentinnikolaev.jdbccrud.models;

import java.util.ArrayList;
import java.util.List;

public class User {
    private Long   id;
    private String firstName;
    private String lastName;
    private Region region;
    private Role   role;
    List<Post> posts;

    private final Role DEFAULT_ROLE = Role.USER;

    public User(Long id, String firstName, String lastName, Region region) {
        this.id        = id;
        this.firstName = firstName;
        this.lastName  = lastName;
        this.role      = DEFAULT_ROLE;
        this.region    = region;
        posts          = new ArrayList<>();
    }

    public User(Long id, String firstName, String lastName, Region region, Role role) {
        this.id        = id;
        this.firstName = firstName;
        this.lastName  = lastName;
        this.region    = region;
        this.role      = role;
        posts          = new ArrayList<>();
    }

    public User(Long id, String firstName, String lastName, Region region, Role role,
                List<Post> posts) {
        this.id        = id;
        this.firstName = firstName;
        this.lastName  = lastName;
        this.region    = region;
        this.role      = role;
        this.posts     = posts;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Region getRegion() {
        return region;
    }

    public Role getRole() {
        return role;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public User setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public User setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public User setRegion(Region region) {
        this.region = region;
        return this;
    }

    public User changeUserRole(String role) {
        this.role = Role.valueOf(role);
        return this;
    }

    @Override
    public int hashCode() {
        return this.firstName.hashCode() + this.lastName.hashCode() * 3 + region.hashCode() +
               this.role.toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        if (this.hashCode() != obj.hashCode()) {
            return false;
        }

        if (! (obj instanceof User)) {
            return false;
        }

        User comparingObj = (User) obj;
        return this.firstName.equals(comparingObj.firstName) && this.lastName.equals(
                comparingObj.lastName) && this.role.toString().equals(comparingObj.role.toString());
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", firstName='" + firstName + '\'' + ", lastName='" +
                lastName + '\'' + ", region=" + region + ", role=" + role + '}';
    }
}
