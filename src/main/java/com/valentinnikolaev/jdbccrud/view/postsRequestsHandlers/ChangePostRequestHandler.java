package com.valentinnikolaev.jdbccrud.view.postsRequestsHandlers;

import com.valentinnikolaev.jdbccrud.controller.PostController;
import com.valentinnikolaev.jdbccrud.models.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ChangePostRequestHandler extends PostRequestHandler {

    private PostController postController;

    public ChangePostRequestHandler(@Autowired PostController postController) {
        this.postController = postController;
    }

    @Override
    public void handleRequest(String action, List<String> options){
        if (CHANGE.equals(action)) {
            processRequest(options);
        } else {
            getNextHandler(action, options);
        }
    }

    private void processRequest(List<String> options) {
        int optionsSize = options.size();
        if (optionsSize == 0 || optionsSize == 1 && ! options.get(0).toLowerCase().equals("help")) {
            getErrorMessage();
            return;
        }

        if (options.get(0).toLowerCase().equals("help")) {
            getHelpForChangingPostContentRequest(options);
            return;
        }

        changePost(options);
    }

    private void changePost(List<String> options) {
        Optional<Post> post = this.postController.getPost(options.get(0));
        if (post.isPresent()) {
            String changedPost = String.join(" ", getOptionsWithOutFirst(options));
            this.postController.changePost(options.get(0), changedPost);
        } else {
            System.out.println(
                    "The post with id: " + options.get(0) + " is not exists in repository." +
                            " Check post id and try again.");
        }
    }

    private void getHelpForChangingPostContentRequest(List<String> options) {
        if (options.get(0).equals(HELP)) {
            String helpInfo =
                    "For changing post content it can be used next format of request:\n" + "\t" +
                            CHANGE + " [post id] [new post content]";

            System.out.println(helpInfo);
        } else {
            getErrorMessage();
        }
    }

    private void getErrorMessage() {
        System.out.println(
                "Invalid request format. Please, check request format and try again, or get " +
                        "help information.");
    }
}
