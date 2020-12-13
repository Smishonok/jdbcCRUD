package com.valentinnikolaev.jdbccrud.view.regionRequestsHandlers;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HelpRegionRequestHandler extends RegionRequestHandler {

    public HelpRegionRequestHandler(){}

    @Override
    public void handleRequest(String action, List<String> options) {
        if (HELP.equals(action)) {
            String helpInfo =
                    "This is the part of the console app in which you can add, change and " +
                            "remove regions data from repository. The main commands are:\n" +
                            "\tadd - adding new region;\n" +
                            "\tget - getting region data from repository;\n" +
                            "\tchange - changing region data in repository;\n" +
                            "\tremove - removing region from repository;\n" +
                            "\n\tCalling \"help\" after each of commands calls the help`s information for the" +
                            " corresponding command.";
            System.out.println(helpInfo);
        } else {
            getNextHandler(action, options);
        }
    }
}
