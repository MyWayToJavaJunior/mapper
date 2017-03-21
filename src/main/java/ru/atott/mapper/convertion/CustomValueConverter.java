package ru.atott.mapper.convertion;

public interface CustomValueConverter {

    Object convertToObject(Object value, Object context);

    default Object convertToSource(Object value) {
        throw new UnsupportedOperationException();
    }
}
