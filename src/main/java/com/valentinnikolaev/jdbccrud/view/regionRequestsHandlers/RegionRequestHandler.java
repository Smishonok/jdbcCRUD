package com.valentinnikolaev.jdbccrud.view.regionRequestsHandlers;

import com.valentinnikolaev.jdbccrud.view.RequestHandler;

public abstract class RegionRequestHandler extends RequestHandler {

    //Parameters of the method
    protected final String ID   = "id";
    protected final String NAME = "name";
    protected final String ALL = "all";

    @Override
    public void getHelp() {
        String helpInfo = "Invalid request type, please check request type and try again.\n" +
                "Help information:\n" +
                "This is the part of the console app in which you can add, change and " +
                "remove region data from repository. The main commands are:\n" +
                "\tadd - adding new region;\n" + "\tget - getting region data from repository;\n" +
                "\tchange - changing region data in repository\n" +
                "\tremove - removing region from repository;\n" +
                "\n\tCalling \"help\" after each of commands calls the help`s information for the" +
                " corresponding command.";
        System.out.println(helpInfo);
    }
}
