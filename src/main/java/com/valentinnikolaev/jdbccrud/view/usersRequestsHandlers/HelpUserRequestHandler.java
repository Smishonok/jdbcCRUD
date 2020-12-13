package com.valentinnikolaev.jdbccrud.view.usersRequestsHandlers;

import com.valentinnikolaev.jdbccrud.controller.RegionController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HelpUserRequestHandler extends UserRequestHandler {

    public HelpUserRequestHandler(@Autowired RegionController regionController) {
        super(regionController);
    }

    @Override
    public void handleRequest(String action, List<String> options) {
        if (HELP.equals(action)) {
            String helpInfo =
                    "This is the part of the console app in which you can add, change and " +
                            "remove user data from repository. The main commands are:\n" +
                            "\tadd - adding new user;\n" +
                            "\tget - getting user data from repository;\n" +
                            "\tchange - changing user data in repository\n" +
                            "\tremove - removing user from repository;\n" +
                            "\n\tCalling \"help\" after each of commands calls the help`s information for the" +
                            " corresponding command.";
            System.out.println(helpInfo);
        } else {
            getNextHandler(action, options);
        }
    }
}
