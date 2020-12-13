package com.valentinnikolaev.jdbccrud.view.regionRequestsHandlers;

import com.valentinnikolaev.jdbccrud.controller.RegionController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RemoveRegionRequestHandler extends RegionRequestHandler {

    private RegionController regionController;

    public RemoveRegionRequestHandler(@Autowired RegionController regionController) {
        this.regionController = regionController;
    }

    @Override
    public void handleRequest(String action, List<String> options) {
        if (REMOVE.equals(action)) {
            processRequest(options);
        } else {
            getNextHandler(action, options);
        }
    }

    private void processRequest(List<String> options) {
        String requestType = "";
        if (options.size() != 0) {
            requestType = options.get(0);
        }

        List<String> requestOptions = getOptionsWithOutFirst(options);

        switch (requestType) {
            case HELP:
                getHelpForRemovingRegionDataRequest();
                break;
            case ID:
                removeRegionById(requestOptions);
                break;
            case ALL:
                removeAllRegions();
                break;
            default:
                System.out.println(
                        "Invalid request type. Please, check request and try again, or " +
                                "call \""+REMOVE+" "+HELP+"\".");
                break;
        }
    }

    private void removeRegionById(List<String> requestOptions) {
        if (requestOptions.size() == 0) {
            System.out.println(
                    "The request does not contain parameter`s values. Please, check the " +
                            "request and try again, or take help information.\n");
            return;
        }

        String regionId = requestOptions.get(0);
        if (isLong(regionId)) {
            this.regionController.removeRegionWithId(regionId);
        } else {
            System.out.println(
                    "The region`s id should consist only of numbers. Please, check the region`s id and try again.");
        }
    }

    private void removeAllRegions() {
        this.regionController.removeAllRegions();
    }

    private void getHelpForRemovingRegionDataRequest() {
        String helpInfo =
                "For removing region`s name it can be used next format of request:\n" + "\t1: " +
                        REMOVE + " " + ID + " [id number] - remove region with requested id;\n" +
                        "\t2: " + REMOVE + " " + ALL + " - remove all regions from repository.\n";

        System.out.println(helpInfo);
    }
}
