package ru.atott.mapper.test;

import org.junit.Assert;
import org.junit.Test;
import ru.atott.mapper.Mapper;
import ru.atott.mapper.MapperFactory;
import ru.atott.mapper.convertion.CustomValueConverter;
import ru.atott.mapper.convertion.DefaultValueConverter;
import ru.atott.mapper.convertion.ValueConverter;
import ru.atott.mapper.dump.CustomDump;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

public class CustomValueConverterTest {


    public static ZonedDateTime now = ZonedDateTime.now();

    @Test
    public void test() throws Exception {
        Map<String, Object> source = new HashMap<String, Object>() {{
            put("id", "idValue");
        }};
        MyFactoryMapper factoryMapper = new MyFactoryMapper();
        Mapper<CustomDump> mapper = factoryMapper.createMapper(CustomDump.class);
        CustomDump customDump = mapper.serializeToObject(source);
        Assert.assertNotNull(customDump);
        Assert.assertEquals("idValue", customDump.getId());
        Assert.assertEquals(now, customDump.getDateTime());
    }

    public static class MyFactoryMapper extends MapperFactory {

        @Override
        protected ValueConverter initializeValueConverter() {
            return new MyDefaultValueConverter(this);
        }
    }

    public static class MyDefaultValueConverter extends DefaultValueConverter {

        public MyDefaultValueConverter(MapperFactory mapperFactory) {
            super(mapperFactory);
        }

        @Override
        protected CustomValueConverter initializeCustomValueConverter(Class tClass) {
            if (tClass.equals(ZonedDateTime.class)) {
                return new CustomValueConverter() {
                    @Override
                    public Object convertToObject(Object value) {
                        return now;
                    }
                };
            }

            return super.initializeCustomValueConverter(tClass);
        }
    }
}
