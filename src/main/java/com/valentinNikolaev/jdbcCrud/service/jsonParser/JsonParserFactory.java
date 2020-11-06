package com.valentinNikolaev.jdbcCrud.service.jsonParser;

public abstract class JsonParserFactory<T> {

    public static <T> JsonParserFactory<T> getFactory(Class<T> entity) {
        switch (entity.getSimpleName()) {
            case "User":
                return new UserJsonParserFactory<>();
            case "Post":
                return new PostJsonParserFactory<>();
            case "Region":
                return new RegionJsonParserFactory<>();
            default:
                throw new IllegalArgumentException("Illegal type of entity.");
        }
    }

    public abstract JsonParser<T> getParser();
}
