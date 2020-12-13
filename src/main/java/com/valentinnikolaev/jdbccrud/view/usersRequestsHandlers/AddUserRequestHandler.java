package com.valentinnikolaev.jdbccrud.view.usersRequestsHandlers;

import com.valentinnikolaev.jdbccrud.controller.RegionController;
import com.valentinnikolaev.jdbccrud.controller.UserController;
import com.valentinnikolaev.jdbccrud.models.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AddUserRequestHandler extends UserRequestHandler {

    private UserController userController;

    public AddUserRequestHandler(@Autowired RegionController regionController,
                                  @Autowired UserController userController) {
        super(regionController);
        this.userController = userController;
    }

    @Override
    public void handleRequest(String action, List<String> options) {
        if (ADD.equals(action)) {
            addUser(options);
        } else {
            getNextHandler(action, options);
        }
    }

    private void addUser(List<String> options) {
        int userDataLength = options.size();

        if (options.size() == 1 && options.get(0).equals(HELP)) {
            userDataLength = - 1;
        }

        switch (userDataLength) {
            case - 1:
                getHelpForAddingRequest();
                break;
            case 3:
                addUserShort(options);
                break;
            case 4:
                addUserLong(options);
                break;
            default:
                System.out.println(
                        "Invalid user data. Pleas, check user data and try again, or call " +
                                "\"add help\".");
                break;
        }
    }

    private void addUserShort(List<String> options) {
        String userFirstName = options.get(0);
        String userLastName  = options.get(1);
        String regionName    = options.get(2);

        if (isRegionNameValid(regionName)) {
            userController.addUser(userFirstName, userLastName, regionName);
        } else {
            System.out.println(
                    "Invalid region name. The region with name: " + regionName + " is not " +
                            "contains in the repository.");
        }
    }

    private void addUserLong(List<String> options) {
        String userFirstName = options.get(0);
        String userLastName  = options.get(1);
        String userRole      = options.get(2);
        String regionName    = options.get(3);

        if (! isRegionNameValid(regionName)) {
            System.out.println(
                    "Invalid region name. The region with name: " + regionName + " is not " +
                            "contains in the repository.");
        }

        if (! isRoleNameValid(userRole)) {
            System.out.println("Invalid role name. User`s role can be:\n");
            List<Role> userRoles = List.of(Role.values());
            for (Role role : userRoles) {
                System.out.println("\t" + role.toString());
            }
        }

        if (isRegionNameValid(regionName) && isRoleNameValid(userRole)) {
            userController.addUser(userFirstName, userLastName, userRole, regionName);
        }
    }

    private void getHelpForAddingRequest() {
        String helpInfo = "For adding user in repository it can be used two formats of data:\n" +
                "\tVariant 1: [user first name] [user last name] [user region]\n" +
                "\tVariant 2: [user first name] [user last name] [user role] [user region]\n" +
                "For example:\n" +
                "\t Variant 1: Ivan Ivanov Moscow\n" +
                "\t Variant 2: Ivan Ivanov admin Moscow\n";
        System.out.println(helpInfo);
    }
}
