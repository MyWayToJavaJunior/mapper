package ru.atott.mapper.convertion;

public interface ValueProducer {

    default Object prepareToObjectSourceValue(Object value) {
        return value;
    }

    default boolean isCustomSerializationToObject() {
        return false;
    }

    default Object serializeToObject(Object sourceValue) {
        return null;
    }
}
