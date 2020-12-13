package com.valentinnikolaev.jdbccrud.view.postsRequestsHandlers;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HelpPostRequestHandler extends PostRequestHandler {

    public HelpPostRequestHandler() {
    }

    @Override
    public void handleRequest(String action, List<String> options) {
        if (HELP.equals(action)) {
            String helpInfo =
                    "This is the part of the console app in which you can add, change and " +
                            "remove posts from repository. The main commands are:\n" +
                            "\tadd - adding new post;\n" +
                            "\tget - getting posts from repository;\n" +
                            "\tchange - changing posts in repository;\n" +
                            "\tremove - removing posts from repository;\n" + "\n\tCalling \"" +
                            HELP + "\" after each of commands calls the help`s " +
                            "information for the corresponding command.";
            System.out.println(helpInfo);
        } else {
            getNextHandler(action, options);
        }
    }
}
