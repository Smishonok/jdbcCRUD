package com.valentinnikolaev.jdbccrud.utils.jsonparser;

import com.google.gson.*;
import com.valentinnikolaev.jdbccrud.models.Post;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PostsListConverter
        implements JsonSerializer<List<Post>>, JsonDeserializer<List<Post>> {
    @Override
    public List<Post> deserialize(JsonElement jsonElement, Type type,
                                  JsonDeserializationContext context) throws JsonParseException {
        JsonArray jsonArray = jsonElement.getAsJsonArray();

        return Stream
                .of(jsonArray)
                .flatMap(StreamProviderForGson::getObjectStream)
                .map(jsonObject->(Post) context.deserialize(jsonObject, Post.class))
                .collect(Collectors.toList());
    }

        @Override
    public JsonElement serialize(List<Post> posts, Type type, JsonSerializationContext context) {
        return posts
                .stream()
                .map(post->context.serialize(post, Post.class))
                .collect(JsonArray::new, JsonArray::add, JsonArray::add);
    }
}
