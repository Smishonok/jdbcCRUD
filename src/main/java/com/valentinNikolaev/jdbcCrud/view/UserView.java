package com.valentinNikolaev.jdbcCrud.view;

import com.valentinNikolaev.jdbcCrud.view.usersRequestsHandlers.*;

import java.util.List;

public class UserView {

    private RequestHandler requestHandler;

    public UserView() throws ClassNotFoundException {
        this.requestHandler = new HelpUserRequestHandler(new AddUserRequestHandler(
                new ChangeUserRequestHandler(
                        new GetUserRequestHandler(new RemoveUserRequestsHandler()))));
    }

    public void action(String action, List<String> options) throws ClassNotFoundException {
        requestHandler.handleRequest(action, options);
    }
}
