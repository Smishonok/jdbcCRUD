package com.valentinNikolaev.jdbcCrud.service.jsonParser;

public class UserJsonParserFactory<T> extends JsonParserFactory<T> {

    @Override
    public JsonParser<T> getParser() {
        return (JsonParser<T>) new UserParser();
    }
}
