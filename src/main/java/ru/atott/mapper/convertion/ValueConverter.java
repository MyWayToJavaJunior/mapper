package ru.atott.mapper.convertion;

import java.util.Map;

public interface ValueConverter {

    String convertToString(Object value);

    byte convertToByte(Object value);

    short convertToShort(Object value);

    int convertToInt(Object value);

    long convertToLong(Object value);

    float convertToFloat(Object value);

    double convertToDouble(Object value);

    boolean convertToBoolean(Object value);

    char convertToChar(Object value);

    Byte convertToByteObject(Object value);

    Short convertToShortObject(Object value);

    Integer convertToInteger(Object value);

    Long convertToLongObject(Object value);

    Float convertToFloatObject(Object value);

    Double convertToDoubleObject(Object value);

    Character convertToCharacter(Object value);

    Boolean convertToBooleanObject(Object value);

    /**
     * Используется при конвертировании исходного значения в Map-у в методу toObject.
     */
    Map convertToMap(Object value);

    /**
     * Используется при исходного значения в Map-у в методе toMap
     */
    Map convertToMap(Object value, Class tClass);

    Object convertToObject(Object value, Class tClass);

    CustomValueConverter getCustomValueConverter(Class tClass);
}
