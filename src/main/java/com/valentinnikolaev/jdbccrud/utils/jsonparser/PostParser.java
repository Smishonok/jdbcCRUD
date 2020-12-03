package com.valentinnikolaev.jdbccrud.utils.jsonparser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.valentinnikolaev.jdbccrud.models.Post;

import java.lang.reflect.Type;
import java.util.List;

public class PostParser implements JsonParser<Post> {

    private Type type = new TypeToken<List<Post>>() {}.getType();
    private Gson parser = new GsonBuilder()
            .registerTypeAdapter(type, new PostsListConverter())
            .create();

    @Override
    public List<Post> parseList(String text) {
        return parser.fromJson(text,type);
    }

    @Override
    public String serialise(List<Post> entities) {
        return parser.toJson(entities,type);
    }
}
