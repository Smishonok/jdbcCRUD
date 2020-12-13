package com.valentinnikolaev.jdbccrud.view.regionRequestsHandlers;

import com.valentinnikolaev.jdbccrud.controller.RegionController;
import com.valentinnikolaev.jdbccrud.models.Region;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ChangeRegionRequestHandler extends RegionRequestHandler {

    private RegionController regionController;

    public ChangeRegionRequestHandler(@Autowired RegionController regionController) {
        this.regionController = regionController;
    }

    @Override
    public void handleRequest(String action, List<String> options) {
        if (CHANGE.equals(action)) {
            processRequest(options);
        } else {
            getNextHandler(action, options);
        }
    }

    private void processRequest(List<String> requestOptions) {
        if (requestOptions.size() == 0) {
            System.out.println(
                    "The request does not contain parameter`s values. Please, check the " +
                            "request and try again, or take help information.\n");
            return;
        }

        if (requestOptions.get(0).equals(HELP)) {
            getHelpForChangingRegionDataRequest();
            return;
        }

        if (requestOptions.size() == 2) {
            String regionId   = requestOptions.get(0);
            String regionName = requestOptions.get(1);
            changeRegionName(regionId, regionName);
        } else {
            System.out.println("Invalid request`s format. Please, check the request and try " +
                                       "again, or take help information.\n");
        }
    }

    private void changeRegionName(String regionId, String regionName) {
        if (! isLong(regionId)) {
            System.out.println(
                    "The region`s id should consist only of numbers. Please, check the region`s id " +
                            "and try again.");
            return;
        }

        Optional<Region> region = this.regionController.getRegionById(regionId);
        if (region.isPresent()) {
            this.regionController.changeRegionName(regionId, regionName);
        } else {
            System.out.println(
                    "The repository does not contain the region with ID: " + regionId + "\n");
        }
    }

    private void getHelpForChangingRegionDataRequest() {
        String helpInfo =
                "For changing region`s name it can be used next format of request:\n" + "\t" +
                        CHANGE + " [id number] [new region name]";

        System.out.println(helpInfo);
    }
}
