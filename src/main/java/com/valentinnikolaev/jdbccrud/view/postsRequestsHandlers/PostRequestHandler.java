package com.valentinnikolaev.jdbccrud.view.postsRequestsHandlers;

import com.valentinnikolaev.jdbccrud.view.RequestHandler;

public abstract class PostRequestHandler extends RequestHandler {

    //Parameters of the method
    protected final String POST_ID = "id";
    protected final String USER_ID = "user_id";
    protected final String ALL     = "all";

    @Override
    public void getHelp() {
        String helpInfo = "Invalid request type, please check request type and try again.\n" +
                "Help information:\n" +
                "This is the part of the console app in which you can add, change and " +
                "remove posts from repository. The main commands are:\n" +
                "\tadd - adding new post;\n" + "\tget - getting posts from repository;\n" +
                "\tchange - changing posts in repository\n" +
                "\tremove - removing posts from repository;\n" + "\n\tCalling \"" + HELP +
                "\" after each of commands calls the help`s information for " +
                "the corresponding command.";
        System.out.println(helpInfo);
    }
}
