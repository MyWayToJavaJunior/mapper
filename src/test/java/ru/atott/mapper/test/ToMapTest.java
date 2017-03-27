package ru.atott.mapper.test;

import org.junit.Assert;
import org.junit.Test;
import ru.atott.mapper.Mapper;
import ru.atott.mapper.MapperFactory;
import ru.atott.mapper.dump.ComplexListDump;
import ru.atott.mapper.dump.ListDump;
import ru.atott.mapper.dump.NestedDump;
import ru.atott.mapper.dump.SimpleDump;

import java.util.*;

public class ToMapTest {

    @Test
    public void test1() throws Exception {
        SimpleDump dump = new SimpleDump();
        dump.setId("10");

        MapperFactory mapperFactory = new MapperFactory();
        Mapper<SimpleDump> mapper = mapperFactory.createMapper(SimpleDump.class);
        Map<String, Object> map = mapper.serializeToMap(dump);

        Assert.assertNotNull(map);
        Assert.assertEquals("10", map.get("id"));
    }

    @Test
    public void test2() throws Exception {
        NestedDump nestedDump = new NestedDump();
        nestedDump.setId("12");
        nestedDump.setSimpleDump(new SimpleDump());
        nestedDump.getSimpleDump().setId("14");
        nestedDump.setSimpleDumpOptional(Optional.of(new SimpleDump()));
        nestedDump.getSimpleDumpOptional().get().setId("16");

        MapperFactory mapperFactory = new MapperFactory();
        Mapper<NestedDump> mapper = mapperFactory.createMapper(NestedDump.class);
        Map<String, Object> map = mapper.serializeToMap(nestedDump);

        Assert.assertNotNull(map);
        Assert.assertEquals("12", map.get("id"));
        Assert.assertNotNull(map.get("simpleDump"));
        Assert.assertEquals("14", ((Map) map.get("simpleDump")).get("id"));
        Assert.assertEquals("16", ((Map) map.get("simpleDumpOptional")).get("id"));
    }

    @Test
    public void test3() throws Exception {
        List<String> sourceList1 = new ArrayList<String>() {{
            add("a");
            add("b");
            add("c");
        }};

        ListDump listDump = new ListDump();
        listDump.setList1(sourceList1);

        MapperFactory mapperFactory = new MapperFactory();
        Mapper<ListDump> mapper = mapperFactory.createMapper(ListDump.class);
        Map<String, Object> map = mapper.serializeToMap(listDump);

        Assert.assertNotNull(map);

        List list = (List) map.get("list1");
        Assert.assertNotNull(list);
        Assert.assertEquals(3, list.size());
        Assert.assertTrue(sourceList1 != list);
    }

    @Test
    public void test4() throws Exception {
        List<SimpleDump> list = Arrays.asList(
                new SimpleDump("1"),
                new SimpleDump("2"),
                new SimpleDump("3")
        );

        ComplexListDump dump = new ComplexListDump(list);
        dump.setOptionalList2(Optional.of(list));

        MapperFactory mapperFactory = new MapperFactory();
        Mapper<ComplexListDump> mapper = mapperFactory.createMapper(ComplexListDump.class);
        Map<String, Object> map = mapper.serializeToMap(dump);

        Assert.assertNotNull(map);
        List mapList = (List) map.get("list");
        Assert.assertNotNull(mapList);
        Assert.assertEquals(3, mapList.size());

        Assert.assertNotNull(map);
        mapList = (List) map.get("optionalList1");
        Assert.assertNull(mapList);

        Assert.assertNotNull(map);
        mapList = (List) map.get("optionalList2");
        Assert.assertNotNull(mapList);
        Assert.assertEquals(3, mapList.size());
    }
}
