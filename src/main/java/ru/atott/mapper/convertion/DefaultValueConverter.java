package ru.atott.mapper.convertion;

import ru.atott.mapper.Mapper;
import ru.atott.mapper.MapperFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class DefaultValueConverter implements ValueConverter {

    private MapperFactory mapperFactory;

    private Map<Class, Optional<CustomValueConverter>> customValueConverterMap = new HashMap<>();

    private final Object customValueConverterMapMonitor = new Object();

    public DefaultValueConverter(MapperFactory mapperFactory) {
        mapperFactory = Objects.requireNonNull(mapperFactory);

        this.mapperFactory = mapperFactory;
    }

    @Override
    public String convertToString(Object value) {
        return (String) value;
    }

    @Override
    public byte convertToByte(Object value) {
        if (value == null) {
            return 0;
        }

        return ((Number) value).byteValue();
    }

    @Override
    public short convertToShort(Object value) {
        if (value == null) {
            return 0;
        }

        return ((Number) value).shortValue();
    }

    @Override
    public int convertToInt(Object value) {
        if (value == null) {
            return 0;
        }

        return ((Number) value).intValue();
    }

    @Override
    public long convertToLong(Object value) {
        if (value == null) {
            return 0;
        }

        return ((Number) value).longValue();
    }

    @Override
    public float convertToFloat(Object value) {
        if (value == null) {
            return 0;
        }

        return ((Number) value).floatValue();
    }

    @Override
    public double convertToDouble(Object value) {
        if (value == null) {
            return 0;
        }

        return ((Number) value).doubleValue();
    }

    @Override
    public boolean convertToBoolean(Object value) {
        if (value == null) {
            return false;
        }

        return (boolean) value;
    }

    @Override
    public char convertToChar(Object value) {
        if (value == null) {
            return 0;
        }

        return (char) value;
    }

    @Override
    public Byte convertToByteObject(Object value) {
        if (value == null) {
            return null;
        }

        return convertToByte(value);
    }

    @Override
    public Short convertToShortObject(Object value) {
        if (value == null) {
            return null;
        }

        return convertToShort(value);
    }

    @Override
    public Integer convertToInteger(Object value) {
        if (value == null) {
            return null;
        }

        return convertToInt(value);
    }

    @Override
    public Long convertToLongObject(Object value) {
        if (value == null) {
            return null;
        }

        return convertToLong(value);
    }

    @Override
    public Float convertToFloatObject(Object value) {
        if (value == null) {
            return null;
        }

        return convertToFloat(value);
    }

    @Override
    public Double convertToDoubleObject(Object value) {
        if (value == null) {
            return null;
        }

        return convertToDouble(value);
    }

    @Override
    public Character convertToCharacter(Object value) {
        if (value == null) {
            return null;
        }

        return convertToChar(value);
    }

    @Override
    public Boolean convertToBooleanObject(Object value) {
        if (value == null) {
            return null;
        }

        return convertToBoolean(value);
    }

    @Override
    public Map convertToMap(Object value) {
        if (value == null) {
            return null;
        }

        return (Map) value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object convertToObject(Object value, Class tClass) {
        try {
            if (value == null) {
                return null;
            }

            Mapper mapper = mapperFactory.createMapper(tClass);
            return mapper.serializeToObject((Map<String, Object>) value);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public final CustomValueConverter getCustomValueConverter(Class tClass) {
        Optional<CustomValueConverter> result = customValueConverterMap.get(tClass);

        if (result == null) {
            synchronized (customValueConverterMapMonitor) {
                result = Optional.ofNullable(initializeCustomValueConverter(tClass));
                customValueConverterMap.put(tClass, result);
            }
        }

        return result.orElse(null);
    }

    protected CustomValueConverter initializeCustomValueConverter(Class tClass) {
        return null;
    }
}
