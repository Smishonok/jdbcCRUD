package com.valentinnikolaev.jdbccrud.controller;

import com.valentinnikolaev.jdbccrud.models.Region;
import com.valentinnikolaev.jdbccrud.models.Role;
import com.valentinnikolaev.jdbccrud.models.User;
import com.valentinnikolaev.jdbccrud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Scope ("singleton")
public class UserController {

    private UserRepository usersRepository;
    private RegionController regionController;

    public UserController(@Autowired RegionController regionController,
                          @Autowired UserRepository userRepository) {
        this.regionController = regionController;
        this.usersRepository  = userRepository;
    }

    public void addUser(String firstName, String lastName, String regionName) {
        addUser(firstName, lastName, "USER", regionName);
    }

    public void addUser(String firstName, String lastName, String roleName, String regionName) {
        Optional<Region> regionOptional = regionController.getRegionByName(regionName);
        if (regionOptional.isEmpty()) {
            System.out.printf("Error: region with name %1$s is not exist in database", regionName);
            return;
        }

        long userId = getLastUserId() + 1;
        Optional<User> userOptional = usersRepository.add(
                new User(userId, firstName, lastName, regionOptional.get(),
                         Role.valueOf(roleName)));

        if (userOptional.isEmpty()) {
            System.out.println("User was not added into database");
        } else {
            System.out.println("User added into database successfully.");
        }
    }

    public Optional<User> getUserById(String id) {
        long userId = Long.parseLong(id);
        Optional<User> user = this.usersRepository.isContains(userId)
                              ? usersRepository.get(userId)
                              : Optional.empty();

        return user;
    }

    public List<User> getAllUsersList() {
        return this.usersRepository.getAll();
    }

    public List<User> getUsersWithFirstName(String firstName) {
        return this.usersRepository
                .getAll()
                .stream()
                .filter(user->user.getFirstName().equals(firstName))
                .collect(Collectors.toList());
    }

    public List<User> getUsersWithLastName(String lastName) {
        return this.usersRepository
                .getAll()
                .stream()
                .filter(user->user.getLastName().equals(lastName))
                .collect(Collectors.toList());
    }

    public List<User> getUsersWithRole(String roleName) {
        return this.usersRepository
                .getAll()
                .stream()
                .filter(user->user.getRole().toString().equals(roleName))
                .collect(Collectors.toList());
    }

    public List<User> getUsersFrom(String regionName) {
        return this.usersRepository
                .getAll()
                .stream()
                .filter(user->user.getRegion().getName().equals(regionName))
                .collect(Collectors.toList());
    }

    public boolean changeUserFirstName(String userId, String newUserFirstName) {
        long id = Long.parseLong(userId);


        if (this.usersRepository.isContains(id)) {
            User user = this.usersRepository.get(id);
            user.setFirstName(newUserFirstName);
            this.usersRepository.change(user);
        }
        return newUserFirstName.equals(this.usersRepository.get(id).getFirstName());
    }

    public boolean changeUserLastName(String userId, String newUserLastName) {
        long id = Long.parseLong(userId);
        if (this.usersRepository.isContains(id)) {
            User user = this.usersRepository.get(id);
            user.setLastName(newUserLastName);
            this.usersRepository.change(user);
        }
        return newUserLastName.equals(this.usersRepository.get(id).getLastName());
    }

    public boolean changeUserRole(String userId, String newUserRole) {
        long id = Long.parseLong(userId);
        if (this.usersRepository.isContains(id)) {
            User user = this.usersRepository.get(id);
            user.changeUserRole(newUserRole);
            this.usersRepository.change(user);
        }
        return newUserRole.equals(this.usersRepository.get(id).getRole().toString());
    }

    public boolean changeUserRegion(String userId, String regionName) {
        long id = Long.parseLong(userId);
        Region region = regionController.getRegionByName(regionName).get();
        if (this.usersRepository.isContains(id)) {
            User user = this.usersRepository.get(id);
            user.setRegion(region);
            this.usersRepository.change(user);
        }
        return regionName.equals(this.usersRepository.get(id).getRegion().getName());
    }

    public boolean removeUser(String userId) {
        return this.usersRepository.remove(Long.parseLong(userId));
    }

    public boolean removeAllUsers() {
        return this.usersRepository.removeAll();
    }

    private long getLastUserId() {
        Optional<Long> maxUserId = getAllUsersList().stream().map(User::getId).max(Long::compareTo);
        return maxUserId.isPresent()
               ? maxUserId.get()
               : 0;
    }
}
