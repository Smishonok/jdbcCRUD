package com.valentinNikolaev.jdbcCrud.view.usersRequestsHandlers;

import com.valentinNikolaev.jdbcCrud.view.RequestHandler;

import java.util.List;

public class HelpUserRequestHandler extends UserRequestHandler {

    public HelpUserRequestHandler(){}

    public HelpUserRequestHandler(RequestHandler nextRequestHandler) {
        super(nextRequestHandler);
    }

    @Override
    public void handleRequest(String action, List<String> options) throws ClassNotFoundException {
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
