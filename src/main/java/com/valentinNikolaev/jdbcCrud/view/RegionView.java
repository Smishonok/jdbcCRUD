package com.valentinNikolaev.jdbcCrud.view;

import com.valentinNikolaev.jdbcCrud.view.regionRequestsHandlers.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RegionView {

    private RequestHandler requestHandler;

    public RegionView(@Autowired HelpRegionRequestHandler helpRegionRequestHandler,
                      @Autowired AddRegionRequestHandler addRegionRequestHandler,
                      @Autowired ChangeRegionRequestHandler changeRegionRequestHandler,
                      @Autowired GetRegionRequestHandler getRegionRequestHandler,
                      @Autowired RemoveRegionRequestHandler removeRegionRequestHandler) {
        this.requestHandler = helpRegionRequestHandler;
        helpRegionRequestHandler
                .setNextHandler(addRegionRequestHandler)
                .setNextHandler(changeRegionRequestHandler)
                .setNextHandler(getRegionRequestHandler)
                .setNextHandler(removeRegionRequestHandler);
    }

    public void action(String action, List<String> options) throws ClassNotFoundException {
        requestHandler.handleRequest(action, options);
    }
}
