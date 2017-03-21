package ru.atott.mapper;

import java.util.Map;

public interface Mapper<T> {

    T newInstance();

    T serializeToObject(Map<String, Object> source);
}
