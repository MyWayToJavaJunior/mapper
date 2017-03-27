package ru.atott.mapper;

import java.util.Map;

public interface Mapper<T> {

    T newInstance();

    default T serializeToObject(Map<String, Object> source) {
        return serializeToObject(source, null);
    }

    T serializeToObject(Map<String, Object> source, Object context);

    default Map<String, Object> serializeToMap(Object source) {
        return serializeToMap(source, null);
    }

    Map<String, Object> serializeToMap(Object source, Object context);
}
