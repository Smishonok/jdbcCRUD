package com.valentinnikolaev.jdbccrud.utils.jsonparser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.valentinnikolaev.jdbccrud.models.Region;

import java.lang.reflect.Type;
import java.util.List;

public class RegionParser implements JsonParser<Region> {

    private Type type = new TypeToken<List<Region>>() {}.getType();
    private Gson parser = new GsonBuilder()
            .registerTypeAdapter(type, new RegionListConverter())
            .create();

    @Override
    public List<Region> parseList(String text) {
        return parser.fromJson(text,type);
    }

    @Override
    public String serialise(List<Region> entities) {
        return parser.toJson(entities,type);
    }
}
