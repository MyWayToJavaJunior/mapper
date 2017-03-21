package ru.atott.mapper.convertion;

public interface CustomValueConverter {

    Object convertToObject(Object value);

    default Object convertToSource(Object value) {
        throw new UnsupportedOperationException();
    }
}
