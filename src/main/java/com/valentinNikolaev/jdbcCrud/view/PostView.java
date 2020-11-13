package com.valentinNikolaev.jdbcCrud.view;

import com.valentinNikolaev.jdbcCrud.view.postsRequestsHandlers.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PostView {

    private RequestHandler requestHandler;

    public PostView(@Autowired HelpPostRequestHandler helpRequestHandler,
                    @Autowired AddPostRequestHandler addPostRequestHandler,
                    @Autowired ChangePostRequestHandler changePostRequestHandler,
                    @Autowired GetPostRequestHandler getPostRequestHandler,
                    @Autowired RemovePostRequestHandler removePostRequestHandler) {
        this.requestHandler = helpRequestHandler;
        helpRequestHandler
                .setNextHandler(addPostRequestHandler)
                .setNextHandler(changePostRequestHandler)
                .setNextHandler(getPostRequestHandler)
                .setNextHandler(removePostRequestHandler);
    }

    public void action(String action, List<String> options) throws ClassNotFoundException {
        requestHandler.handleRequest(action, options);
    }


}
