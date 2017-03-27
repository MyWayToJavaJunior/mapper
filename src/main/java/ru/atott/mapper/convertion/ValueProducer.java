package ru.atott.mapper.convertion;

public interface ValueProducer {

    default Object prepareToObjectSourceValue(Object value, Object sourceMap, Object context) {
        return value;
    }

    default String getToMapFieldName(String sourceFieldName) {
        return sourceFieldName;
    }

    default boolean isCustomSerialization() {
        return false;
    }

    default Object serialzeToObjectValue(Object sourceValue, Object sourceMap, Object context) {
        return null;
    }

    default Object serializeToMapValue(Object sourceValue, Object context) {
        return null;
    }
}
