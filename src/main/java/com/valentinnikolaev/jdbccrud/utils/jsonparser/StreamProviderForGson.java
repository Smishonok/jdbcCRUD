package com.valentinnikolaev.jdbccrud.utils.jsonparser;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class StreamProviderForGson {

    public static Stream<JsonObject> getObjectStream(JsonArray array) {
        List<JsonObject> objects = new ArrayList<>();
        array.forEach(jsonElement -> objects.add(jsonElement.getAsJsonObject()));
        return objects.stream();
    }
}
