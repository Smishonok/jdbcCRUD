package com.valentinNikolaev.jdbcCrud.view;

import com.valentinNikolaev.jdbcCrud.view.usersRequestsHandlers.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserView {

    private RequestHandler requestHandler;

    public UserView(@Autowired HelpUserRequestHandler helpUserRequestHandler,
                    @Autowired AddUserRequestHandler addUserRequestHandler,
                    @Autowired ChangeUserRequestHandler changeUserRequestHandler,
                    @Autowired GetUserRequestHandler getUserRequestHandler,
                    @Autowired RemoveUserRequestsHandler removeUserRequestsHandler) {
        this.requestHandler = helpUserRequestHandler;
        helpUserRequestHandler
                .setNextHandler(addUserRequestHandler)
                .setNextHandler(changeUserRequestHandler)
                .setNextHandler(getUserRequestHandler)
                .setNextHandler(removeUserRequestsHandler);
    }

    public void action(String action, List<String> options) throws ClassNotFoundException {
        requestHandler.handleRequest(action, options);
    }
}
