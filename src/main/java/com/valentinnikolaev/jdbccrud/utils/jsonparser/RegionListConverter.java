package com.valentinnikolaev.jdbccrud.utils.jsonparser;

import com.google.gson.*;
import com.valentinnikolaev.jdbccrud.models.Region;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RegionListConverter
        implements JsonSerializer<List<Region>>, JsonDeserializer<List<Region>> {

    @Override
    public List<Region> deserialize(JsonElement jsonElement, Type type,
                                    JsonDeserializationContext context) throws JsonParseException {
        return Stream
                .of(jsonElement)
                .map(JsonElement::getAsJsonArray)
                .flatMap(StreamProviderForGson::getObjectStream)
                .map(jsonObject->(Region) context.deserialize(jsonObject, Region.class))
                .collect(Collectors.toList());
    }

    @Override
    public JsonElement serialize(List<Region> regions, Type type,
                                 JsonSerializationContext context) {
        return regions
                .stream()
                .map(region->context.serialize(region, Region.class))
                .collect(JsonArray::new, JsonArray::add, JsonArray::add);
    }
}
