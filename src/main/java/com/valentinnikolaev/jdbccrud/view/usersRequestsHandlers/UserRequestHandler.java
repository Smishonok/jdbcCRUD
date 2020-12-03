package com.valentinnikolaev.jdbccrud.view.usersRequestsHandlers;

import com.valentinnikolaev.jdbccrud.controller.RegionController;
import com.valentinnikolaev.jdbccrud.models.Role;
import com.valentinnikolaev.jdbccrud.view.RequestHandler;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public abstract class UserRequestHandler extends RequestHandler {

    private RegionController regionController;

    public UserRequestHandler(@Autowired RegionController regionController) {
        this.regionController = regionController;
    }

    //Parameters of the method
    protected final String ID         = "id";
    protected final String ALL        = "all";
    protected final String FIRST_NAME = "name.first";
    protected final String LAST_NAME  = "name.last";
    protected final String ROLE       = "role";
    protected final String REGION     = "region";

    @Override
    public void getHelp() {
        String helpInfo = "Invalid request type, please check request type and try again.\n" +
                "Help information:\n" +
                "This is the part of the console app in which you can add, change and " +
                "remove user data from repository. The main commands are:\n" +
                "\tadd - adding new user;\n" + "\tget - getting user data from repository;\n" +
                "\tchange - changing user data in repository\n" +
                "\tremove - removing user from repository;\n" +
                "\n\tCalling \"help\" after each of commands calls the help`s information for the" +
                " corresponding command.";
        System.out.println(helpInfo);
    }

    protected boolean isRegionNameValid(String regionName) {
        return regionController.getRegionByName(regionName).isPresent();
    }

    protected boolean isRoleNameValid(String roleName) {
        List<Role> roles = List.of(Role.values());
        return roles.stream().anyMatch(role->role.toString().equals(roleName));
    }
}
