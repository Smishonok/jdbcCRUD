package com.valentinnikolaev.jdbccrud.controller;

import com.valentinnikolaev.jdbccrud.models.Region;
import com.valentinnikolaev.jdbccrud.models.Role;
import com.valentinnikolaev.jdbccrud.models.User;
import com.valentinnikolaev.jdbccrud.repository.RegionRepository;
import com.valentinnikolaev.jdbccrud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Scope ("singleton")
public class UserController {

    private UserRepository usersRepository;
    private RegionRepository regionRepository;

    public UserController(@Autowired RegionRepository regionRepository,
                          @Autowired UserRepository userRepository) {
        this.regionRepository = regionRepository;
        this.usersRepository  = userRepository;
    }

    public void addUser(String firstName, String lastName, String regionName) {
        addUser(firstName, lastName, "USER", regionName);
    }

    public void addUser(String firstName, String lastName, String roleName, String regionName) {
        Optional<Region> regionOptional = regionRepository
                .getAll()
                .stream()
                .filter(region->region.getName().equals(regionName))
                .findAny();

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
        Function<User, Boolean> userParamChangingFunction = user->{
            user.setFirstName(newUserFirstName);
            usersRepository.change(user);
            return usersRepository.get(user.getId()).get().getFirstName().equals(newUserFirstName);
        };

        return changeUserParam(userId, userParamChangingFunction);
    }

    public boolean changeUserLastName(String userId, String newUserLastName) {
        Function<User, Boolean> userParamChangingFunction = user->{
            user.setLastName(newUserLastName);
            usersRepository.change(user);
            return usersRepository.get(user.getId()).get().getLastName().equals(newUserLastName);
        };

        return changeUserParam(userId, userParamChangingFunction);
    }

    public boolean changeUserRole(String userId, String newUserRole) {
        Function<User, Boolean> userParamChangingFunction = user->{
            user.changeUserRole(newUserRole);
            usersRepository.change(user);
            return usersRepository.get(user.getId()).get().getRole().toString().equals(newUserRole);
        };

        return changeUserParam(userId, userParamChangingFunction);
    }

    public boolean changeUserRegion(String userId, String regionName) {
        Function<User, Boolean> userParamChangingFunction = user->{
            Optional<Region> regionOptional = regionRepository
                    .getAll()
                    .stream()
                    .filter(region->region.getName().equals(regionName))
                    .findAny();

            if (regionOptional.isPresent()) {
                user.setRegion(regionOptional.get());
                usersRepository.change(user);
                return usersRepository
                        .get(user.getId())
                        .get()
                        .getRegion()
                        .equals(regionOptional.get());
            } else {
                System.out.printf("Region with name: %1$s is not exist in database. Please, check" +
                                  " region name or add region with name %1$s into " +
                                  "database before changing user parameters.", regionName);
                return false;
            }
        };

        return changeUserParam(userId, userParamChangingFunction);
    }

    private boolean changeUserParam(String userId, Function<User, Boolean> changingFunction) {
        long id = Long.parseLong(userId);
        boolean isParamChanged = false;
        Optional<User> userOptional = usersRepository.get(id);

        if (userOptional.isPresent()) {
            isParamChanged = changingFunction.apply(userOptional.get());
        } else {
            System.out.printf("User with id: %1$d is not exist in database", id);
        }

        return isParamChanged;
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
