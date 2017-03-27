package ru.atott.mapper.test;

import org.junit.Assert;
import org.junit.Test;
import ru.atott.mapper.Mapper;
import ru.atott.mapper.MapperFactory;
import ru.atott.mapper.convertion.ValueProducer;
import ru.atott.mapper.introspection.BeanField;

import java.util.*;
import java.util.function.Supplier;

public class ValueProducerTest {

    static List<String> test = new ArrayList<>();

    public static class TestValueProducer implements ValueProducer {

        @Override
        public Object prepareToObjectSourceValue(Object value, Object sourceMap, Object context) {
            return String.valueOf(value) + "__test";
        }
    }

    public static class ConstantValueProducer implements ValueProducer {

        @Override
        public boolean isCustomSerialization() {
            return true;
        }

        @Override
        public Object serialzeToObjectValue(Object sourceValue, Object sourceMap, Object context) {
            return "constantValue__test";
        }

        @Override
        public Object serializeToMapValue(Object sourceValue, Object context) {
            return "mapValue1";
        }
    }

    public static class ListProducer implements ValueProducer {
        @Override
        public boolean isCustomSerialization() {
            return true;
        }

        @Override
        public Object serialzeToObjectValue(Object sourceValue, Object sourceMap, Object context) {
            return test;
        }

    }

    public static class CustomMapperFactory extends MapperFactory {

        @Override
        protected Optional<ValueProducer> getValueProducer(BeanField field) {
            if (field.getFieldName().equals("id")) {
                return Optional.of(new TestValueProducer());
            }

            if (field.getFieldName().equals("constantValue")) {
                return Optional.of(new ConstantValueProducer());
            }

            if (field.getFieldName().equals("list")) {
                return Optional.of(new ListProducer());
            }

            return super.getValueProducer(field);
        }
    }

    @Test
    public void test1() throws Exception {
        Map<String, Object> source = new HashMap<String, Object>() {{
            put("id", "idValue");
            put("field", "fieldValue");
        }};

        CustomMapperFactory customMapperFactory = new CustomMapperFactory();
        Mapper<SomeBean> mapper = customMapperFactory.createMapper(SomeBean.class);

        SomeBean someBean = mapper.serializeToObject(source);
        Assert.assertNotNull(someBean);
        Assert.assertEquals("idValue__test", someBean.getId());
        Assert.assertEquals("fieldValue", someBean.getField());
        Assert.assertEquals("constantValue__test", someBean.getConstantValue());
        Assert.assertTrue(test == someBean.getList());
    }

    @Test
    public void test2() throws Exception {
        CustomMapperFactory customMapperFactory = new CustomMapperFactory();
        Mapper<SomeBean> mapper = customMapperFactory.createMapper(SomeBean.class);

        Map<String, Object> map = mapper.serializeToMap(new SomeBean());
        Assert.assertNotNull(map);
        Assert.assertEquals("mapValue1", map.get("constantValue"));
    }

    public static class SomeBean {

        private String id;

        private String field;

        private String constantValue;

        private List<String> list;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getConstantValue() {
            return constantValue;
        }

        public void setConstantValue(String constantValue) {
            this.constantValue = constantValue;
        }

        public List<String> getList() {
            return list;
        }

        public void setList(List<String> list) {
            this.list = list;
        }
    }
}
