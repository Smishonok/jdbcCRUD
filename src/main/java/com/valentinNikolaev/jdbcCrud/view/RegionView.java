package com.valentinNikolaev.jdbcCrud.view;

import com.valentinNikolaev.jdbcCrud.view.regionRequestsHandlers.*;

import java.util.List;

public class RegionView {

    private RequestHandler requestHandler;

    public RegionView() throws ClassNotFoundException {
        this.requestHandler = new HelpRegionRequestHandler(new AddRegionRequestHandler(
                new ChangeRegionRequestHandler(
                        new GetRegionRequestHandler(new RemoveRegionRequestHandler()))));
    }

    public void action(String action, List<String> options) throws ClassNotFoundException {
        requestHandler.handleRequest(action, options);
    }
}
