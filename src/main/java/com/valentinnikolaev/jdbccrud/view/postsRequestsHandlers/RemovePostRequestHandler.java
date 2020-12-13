package com.valentinnikolaev.jdbccrud.view.postsRequestsHandlers;

import com.valentinnikolaev.jdbccrud.controller.PostController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RemovePostRequestHandler extends PostRequestHandler {

    private PostController postController;

    public RemovePostRequestHandler(@Autowired PostController postController) {
        this.postController = postController;
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
                getHelpForRemovingPostRequest();
                break;
            case POST_ID:
                removePostById(requestOptions);
                break;
            case USER_ID:
                removePostByUserId(requestOptions);
                break;
            case ALL:
                removeAllPost();
                break;
            default:
                getErrorMessage();
                break;
        }
    }

    private void removePostById(List<String> requestOptions) {
        if (requestOptions.size() == 0) {
            System.out.println(
                    "The request does not contain parameter`s values. Please, check the " +
                            "request and try again, or take help information.\n");
            return;
        }

        String postId = requestOptions.get(0);
        if (isLong(postId)) {
            this.postController.removePost(postId);
        } else {
            System.out.println("The posts`s id should consist only of numbers. Please, check the " +
                                       "post`s id and try again.");
        }
    }

    private void removePostByUserId(List<String> requestOptions) {
        if (requestOptions.size() == 0) {
            System.out.println(
                    "The request does not contain parameter`s values. Please, check the " +
                            "request and try again, or take help information.\n");
            return;
        }

        String userId = requestOptions.get(0);
        if (isLong(userId)) {
            this.postController.removeAllPostByUser(userId);
        } else {
            System.out.println("The posts`s id should consist only of numbers. Please, check the " +
                                       "post`s id and try again.");
        }
    }

    private void removeAllPost() {
        this.postController.removeAllPosts();
    }

    private void getErrorMessage() {
        System.out.println(
                "Invalid request type. Please, check request and try again, or " + "call \"" +
                        REMOVE + " " + HELP + "\".");
    }

    private void getHelpForRemovingPostRequest() {
        String helpInfo =
                "For removing post it can be used next format of request:\n" + "\t1: " + REMOVE +
                        " " + POST_ID + " [id number] - remove post with requested id;" + "\n" +
                        "\t2: " + REMOVE + USER_ID + " [user id number] - remove posts created by" +
                        " user.\n" + "\t3: " + REMOVE + " " + ALL +
                        " - remove all posts from repository.\n";

        System.out.println(helpInfo);
    }
}
