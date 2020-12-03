package com.valentinnikolaev.jdbccrud.utils.jsonparser;

public class UserJsonParserFactory<T> extends JsonParserFactory<T> {

    @Override
    public JsonParser<T> getParser() {
        return (JsonParser<T>) new UserParser();
    }
}
