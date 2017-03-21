package ru.atott.mapper.test;

import org.junit.Assert;
import org.junit.Test;
import ru.atott.mapper.Mapper;
import ru.atott.mapper.MapperFactory;
import ru.atott.mapper.dump.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MapToObjectTest {

    @Test
    public void test1() throws Exception {
        MapperFactory objectSerializerFactory = new MapperFactory();
        Mapper<SimpleDump> serializer = objectSerializerFactory.createMapper(SimpleDump.class);

        SimpleDump dump = serializer.newInstance();
        Assert.assertNotNull(dump);
        Assert.assertEquals("defaultId", dump.getId());
    }

    @Test
    public void test2() throws Exception {
        MapperFactory objectSerializerFactory = new MapperFactory();
        Mapper<SimpleDump> serializer = objectSerializerFactory.createMapper(SimpleDump.class);

        Map<String, Object> source = new HashMap<String, Object>() {{
            put("id", "idValue");
        }};

        SimpleDump dump = serializer.serializeToObject(source);
        Assert.assertNotNull(dump);
        Assert.assertEquals("idValue", dump.getId());
    }

    @Test
    public void test3() throws Exception {
        MapperFactory mapperFactory = new MapperFactory();
        Mapper<OptionalDump> mapper = mapperFactory.createMapper(OptionalDump.class);

        Map<String, Object> source = new HashMap<String, Object>() {{
            put("id", "idValue");
        }};

        OptionalDump dump = mapper.serializeToObject(source);
        Assert.assertNotNull(dump);
        Assert.assertEquals(Optional.of("idValue"), dump.getId());
        Assert.assertNotNull(dump.getUnexistedField());
        Assert.assertFalse(dump.getUnexistedField().isPresent());
    }

    @Test
    public void test4() throws Exception {
        MapperFactory mapperFactory = new MapperFactory();
        Mapper<VcDump> mapper = mapperFactory.createMapper(VcDump.class);

        Map<String, Object> source = new HashMap<String, Object>() {{
            put("stringField", null);
            put("longField", 12);
            put("primitiveLongValue", 14);
        }};

        VcDump dump = mapper.serializeToObject(source);
        Assert.assertNotNull(dump);
        Assert.assertEquals(null, dump.getStringField());
        Assert.assertEquals(12L, (long) dump.getLongField());
        Assert.assertEquals(14L, dump.getPrimitiveLongValue());
    }

    @Test
    public void test5() throws Exception {
        MapperFactory mapperFactory = new MapperFactory();
        Mapper<ListDump> mapper = mapperFactory.createMapper(ListDump.class);

        Map<String, Object> source = new HashMap<String, Object>() {{
            put("list1", Arrays.asList("1", "2"));
        }};

        ListDump dump = mapper.serializeToObject(source);
        Assert.assertNotNull(dump);
        Assert.assertNotNull(dump.getList1());
        Assert.assertNotNull(dump.getList2());
        Assert.assertTrue(dump.getList2().isPresent());
        Assert.assertEquals(2, dump.getList1().size());
        Assert.assertEquals("2", dump.getList1().get(1));
    }

    @Test
    public void test6() throws Exception {
        MapperFactory mapperFactory = new MapperFactory();
        Mapper<NestedDump> mapper = mapperFactory.createMapper(NestedDump.class);

        Map<String, Object> source = new HashMap<String, Object>() {{
            put("id", "test1");
            put("simpleDump", new HashMap<String, Object>() {{
                put("id", "test2");
            }});
        }};

        NestedDump nestedDump = mapper.serializeToObject(source);
        Assert.assertNotNull(nestedDump);
        Assert.assertNotNull(nestedDump.getSimpleDump());
        Assert.assertNotNull(nestedDump.getSimpleDumpOptional());
        Assert.assertFalse(nestedDump.getSimpleDumpOptional().isPresent());
        Assert.assertEquals("test1", nestedDump.getId());
        Assert.assertEquals("test2", nestedDump.getSimpleDump().getId());
    }

    @Test
    public void test7() throws Exception {
        MapperFactory mapperFactory = new MapperFactory();
        Mapper<MapDump> mapper = mapperFactory.createMapper(MapDump.class);

        Map<String, Object> source = new HashMap<String, Object>() {{
            put("id", "test1");
            put("mapField", new HashMap<String, Object>() {{
                put("id", "test2");
            }});
        }};

        MapDump dump = mapper.serializeToObject(source);
        Assert.assertNotNull(dump);
        Assert.assertNotNull(dump.getMapField());
        Assert.assertEquals("test2", dump.getMapField().get("id"));
    }
}
