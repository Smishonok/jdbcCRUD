package com.valentinnikolaev.jdbccrud.view.regionRequestsHandlers;

import com.valentinnikolaev.jdbccrud.controller.RegionController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AddRegionRequestHandler extends RegionRequestHandler {

    private RegionController regionController;

    public AddRegionRequestHandler(@Autowired RegionController regionController) {
        this.regionController = regionController;
    }

    @Override
    public void handleRequest(String action, List<String> options)  {
        if (ADD.equals(action)) {
            processRequest(options);
        } else {
            getNextHandler(action, options);
        }
    }

    private void processRequest(List<String> options) {
        if (options.size() != 1) {
            System.out.println(
                    "Invalid request format. Please, check request format and try again, or get " +
                            "help information.");
            return;
        }

        if (options.get(0).equals(HELP)) {
            getHelpForAddingRegionDataRequest();
        } else {
            this.regionController.addRegion(options.get(0));
        }
    }

    private void getHelpForAddingRegionDataRequest() {
        String helpInfo = "For adding regions into the repository it can be used next formats of" +
                "request:\n" + "\t1: " + ADD + " [region name]";
        System.out.println(helpInfo);
    }
}
