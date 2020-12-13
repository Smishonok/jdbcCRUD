package com.valentinnikolaev.jdbccrud.view;

import java.util.ArrayList;
import java.util.List;

public abstract class RequestHandler {

    protected final String ADD    = "add";
    protected final String GET    = "get";
    protected final String CHANGE = "change";
    protected final String REMOVE = "remove";
    protected final String HELP   = "help";

    private RequestHandler nextRequestHandler;

    public abstract void handleRequest(String action, List<String> options);

    public RequestHandler setNextHandler(RequestHandler handler) {
        this.nextRequestHandler = handler;
        return handler;
    }

    public boolean hasNextHandler() {
        boolean hasNext = false;
        if (nextRequestHandler != null) {
            hasNext = true;
        }
        return hasNext;
    }

    public void getNextHandler(String action, List<String> options) {
        if (hasNextHandler()) {
            nextRequestHandler.handleRequest(action, options);
        } else {
            getHelp();
        }
    }

    public abstract void getHelp();

    public List<String> getOptionsWithOutFirst(List<String> options) {
        if (options.size() > 1) {
            return options.subList(1, options.size());
        } else {
            return new ArrayList<>();
        }
    }

    protected boolean isLong(String string) {
        try {
            Long.parseLong(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
