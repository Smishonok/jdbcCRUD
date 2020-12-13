package com.valentinnikolaev.jdbccrud.view.postsRequestsHandlers;

import com.valentinnikolaev.jdbccrud.controller.PostController;
import com.valentinnikolaev.jdbccrud.controller.UserController;
import com.valentinnikolaev.jdbccrud.models.Post;
import com.valentinnikolaev.jdbccrud.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Component
public class GetPostRequestHandler extends PostRequestHandler {

    private PostController postController;
    private UserController userController;

    public GetPostRequestHandler(@Autowired PostController postController,
                                 @Autowired UserController userController) {
        this.postController = postController;
        this.userController = userController;
    }

    @Override
    public void handleRequest(String action, List<String> options) {
        if (GET.equals(action)) {
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
                getHelpForGettingPostRequest();
                break;
            case POST_ID:
                getPostBuId(requestOptions);
                break;
            case USER_ID:
                getPostsListByUserId(requestOptions);
                break;
            case ALL:
                getAllPosts();
                break;
            default:
                System.out.println("Invalid request type. Please, check request type and try " +
                                   "again, or take help information using \"" + ADD + " " + HELP +
                                   "\".\n");
                break;
        }
    }

    private void getPostBuId(List<String> requestOptions) {
        List<Post> postsList = this.postController.getAllPostsList();
        if (postsList.size() == 0) {
            System.out.println("The repository is empty, no one post created.");
            return;
        }

        if (isOptionsValid(requestOptions) && isIdLong(requestOptions)) {
            Post post = this.postController.getPost(requestOptions.get(0)).get();
            printPost(post);
        }
    }

    private void getPostsListByUserId(List<String> requestOptions) {
        List<Post> postsList = this.postController.getAllPostsList();
        if (postsList.size() == 0) {
            System.out.println("The repository is empty, no one post created.");
            return;
        }

        if (isOptionsValid(requestOptions) && isIdLong(requestOptions) &&
            isUserExists(requestOptions)) {
            List<Post> userPosts = this.postController.getPostsByUserId(requestOptions.get(0));
            userPosts.forEach(this::printPost);
        }
    }

    private void getAllPosts() {
        List<Post> postsList = this.postController.getAllPostsList();
        if (postsList.size() != 0) {
            postsList.forEach(this::printPost);
        } else {
            System.out.println("The repository is empty, no one post created.");
        }
    }

    private void printPost(Post post) {
        String userName = "User id: " + post.getUserId();
        String postId = "Post id: " + post.getId();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy mm:HH:ss");
        String postDate = "Post created: " + post.getDateOfCreation().format(formatter);
        String postUpdatingDate = post.getDateOfLastUpdate().equals(post.getDateOfCreation())
                                  ? ""
                                  : "Post updated: " + post.getDateOfLastUpdate().format(formatter);

        String postView =
                postId + "\n" + userName + "\n" + postDate + "\t" + postUpdatingDate + "\n" + "\t" +
                post.getContent() + "\n";

        System.out.println(postView);
    }

    private boolean isOptionsValid(List<String> requestOptions) {
        boolean isOptionsCorrect = true;
        if (requestOptions.size() != 1) {
            System.out.println(
                    "Invalid request format. Please, check request format and try again, " +
                    "or get help information.");
            isOptionsCorrect = false;
        }

        return isOptionsCorrect;
    }

    private boolean isIdLong(List<String> requestOptions) {
        boolean isOptionsCorrect = true;

        String userIdValue = requestOptions.get(0);
        if (! isLong(userIdValue)) {
            System.out.println("The id number should consist only of numbers. Please, check the " +
                               "id and try again.");
            isOptionsCorrect = false;
        }

        return isOptionsCorrect;
    }

    private boolean isUserExists(List<String> requestOptions) {
        boolean isOptionsCorrect = true;

        String userId = requestOptions.get(0);
        Optional<User> user = this.userController.getUserById(userId);
        if (user.isEmpty()) {
            System.out.println("User with id: " + userId + " is not exists. Please, check the " +
                               "user`s id number and try again.\n");
            isOptionsCorrect = false;
        }
        return isOptionsCorrect;
    }


    private void getHelpForGettingPostRequest() {
        String helpInfo = "For getting post from the repository it can be used next formats of" +
                          "request:\n" + "\t1: " + GET + " " + POST_ID +
                          " [id number] - return the post with requested id\n" + "\t2: " + GET +
                          " " + USER_ID + " [id number] - return list of posts crated by user\n" +
                          "\t3: " + GET + " " + ALL + " - return list of all posts in repository\n";

        System.out.println(helpInfo);
        ;
    }
}
