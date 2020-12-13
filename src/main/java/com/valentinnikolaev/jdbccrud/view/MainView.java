package com.valentinnikolaev.jdbccrud.view;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

@Component
public class MainView {

    private final String POST = "post";
    private final String USER = "user";
    private final String REGION = "region";
    private final String HELP = "help";
    private final String EXIT = "exit";

    private PostView postView;
    private RegionView regionView;
    private UserView userView;
    private String requestType;

    public MainView(@Autowired PostView postView, @Autowired RegionView regionView,
                    @Autowired UserView userView) {
        this.postView   = postView;
        this.regionView = regionView;
        this.userView   = userView;
    }

    public void initiateMainView()  {
        System.out.println(
                "Hello, this is the simpleCRUDE app. This app created  in order to learn " +
                "use to NIO package, streams API, lambda expressions, file`s input and " +
                "output streams.\nThis app can:\n" +
                "\tcreate users, regions, user`s posts and save its in file repository;\n" +
                "\tchange user`s attributes (first name, last name, role, region), and  " +
                "post`s content;\n" + "\tremove data from the repository.\n" +
                "\nFor using this app you should enter the commands in command`s line. " +
                "The commands " + "should contain the words which delimited by white " +
                "spaces.\n" + "\nFor getting help information to enter \"help\"\n\n");
        requestType = "start";
        while (requestType != EXIT) {
            Scanner commandScanner = new Scanner(System.in);
            String request = commandScanner.nextLine();
            if (! request.isEmpty()) {
                processRequest(request);
            }
        }
    }

    private void processRequest(String request) {
        List<String> requestParams = Arrays.asList(request.split(" "));
        String requestType = requestParams.size() > 0
                             ? requestParams.get(0)
                             : "";
        String requestOrder = requestParams.size() > 1
                              ? requestParams.get(1)
                              : "";
        List<String> requestOptions = getRequestOptions(requestParams);

        switch (requestType) {
            case POST:
                this.postView.action(requestOrder, requestOptions);
                break;
            case REGION:
                this.regionView.action(requestOrder, requestOptions);
                break;
            case USER:
                this.userView.action(requestOrder, requestOptions);
                break;
            case HELP:
                getHelpInformation();
                break;
            case EXIT:
                this.requestType = "exit";
                break;
            default:
                getErrorMessage();
                break;
        }
    }

    private List<String> getRequestOptions(List<String> requestParams) {
        List<String> requestOptions = new ArrayList<>();
        if (requestParams.size() > 2) {
            for (int i = 2; i < requestParams.size(); i++) {
                requestOptions.add(requestParams.get(i));
            }
        }
        return requestOptions;
    }

    private void getHelpInformation() {
        String helpInfo = "This is the part of the console app in which you can add, change and " +
                          "remove users, regions and posts from repository. The request format is: [request" +
                          " type] [order] [order options]. The main types of requests are:" +
                          "  :\n" + "\tuser - adding, changing and removing users data\n" +
                          "\tpost - adding, changing and removing posts;\n" +
                          "\tregion - adding, changing and removing regions;\n" + "\n\tCalling \"" +
                          HELP + "\" after each of request type calls the help`s " +
                          "information for the corresponding type.";
        System.out.println(helpInfo);
    }


    private void getErrorMessage() {
        System.out.println(
                "Invalid request type. Please, check request and try again. You can " + "use " +
                HELP + " for getting help or use " + EXIT + " for closing app" + ".\n");
    }
}
