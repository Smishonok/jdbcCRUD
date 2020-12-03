package com.valentinnikolaev.jdbccrud.utils.jsonparser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.valentinnikolaev.jdbccrud.models.User;

import java.lang.reflect.Type;
import java.util.List;

public class UserParser implements JsonParser<User> {

    private Type type = new TypeToken<List<User>>() {}.getType();
    private Gson parser = new GsonBuilder()
            .registerTypeAdapter(type, new UserListConverter())
            .create();

    @Override
    public List<User> parseList(String text) {
        return parser.fromJson(text, type);
    }

    @Override
    public String serialise(List<User> entities) {
        return parser.toJson(entities, type);
    }
}
