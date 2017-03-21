package ru.atott.mapper.convertion;

public interface ValueProducer {

    default Object prepareToObjectSourceValue(Object value, Object context) {
        return value;
    }

    default boolean isCustomSerializationToObject() {
        return false;
    }

    default Object serializeToObject(Object sourceValue, Object context) {
        return null;
    }
}
