package ru.atott.mapper.bm.list;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import ru.atott.mapper.Mapper;
import ru.atott.mapper.MapperFactory;
import ru.atott.mapper.bm.BaseBmTest;
import ru.atott.mapper.dump.ListDump;
import ru.atott.mapper.dump.OneFieldDump;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 57.5 ns
 */
public class JavassistListMapTest extends BaseBmTest {

    @State(Scope.Benchmark)
    public static class BmState {

        Map<String, Object> source = new HashMap<String, Object>() {{
            put("list1", Arrays.asList("1", "2"));
        }};

        Mapper<ListDump> mapper;

        @Setup(Level.Trial)
        public void init() throws Exception {
            MapperFactory factory = new MapperFactory();
            mapper = factory.createMapper(ListDump.class);
        }
    }

    @Benchmark
    public void test(BmState state, Blackhole blackhole) {
        Map<String, Object> source = state.source;
        ListDump dump = state.mapper.serializeToObject(source);
        blackhole.consume(dump);
    }
}
