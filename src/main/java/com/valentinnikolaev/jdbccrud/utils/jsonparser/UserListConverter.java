package com.valentinnikolaev.jdbccrud.utils.jsonparser;

import com.google.gson.*;
import com.valentinnikolaev.jdbccrud.models.User;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserListConverter implements JsonSerializer<List<User>>, JsonDeserializer<List<User>> {
    @Override
    public List<User> deserialize(JsonElement jsonElement, Type type,
                                  JsonDeserializationContext context) throws JsonParseException {
        return Stream
                .of(jsonElement)
                .map(JsonElement::getAsJsonArray)
                .flatMap(StreamProviderForGson::getObjectStream)
                .map(jsonObject->(User) context.deserialize(jsonObject, User.class))
                .collect(Collectors.toList());
    }

    @Override
    public JsonElement serialize(List<User> users, Type type, JsonSerializationContext context) {
        return users
                .stream()
                .map(user->context.serialize(user, User.class))
                .collect(JsonArray::new, JsonArray::add, JsonArray::add);
    }
}
