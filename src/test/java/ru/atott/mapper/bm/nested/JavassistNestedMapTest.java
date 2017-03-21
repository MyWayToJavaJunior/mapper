package ru.atott.mapper.bm.nested;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import ru.atott.mapper.Mapper;
import ru.atott.mapper.MapperFactory;
import ru.atott.mapper.bm.BaseBmTest;
import ru.atott.mapper.dump.ListDump;
import ru.atott.mapper.dump.NestedDump;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 52.93 ns
 */
public class JavassistNestedMapTest extends BaseBmTest {

    @State(Scope.Benchmark)
    public static class BmState {

        Map<String, Object> source = new HashMap<String, Object>() {{
            put("id", "test1");
            put("simpleDump", new HashMap<String, Object>() {{
                put("id", "test2");
            }});
        }};

        Mapper<NestedDump> mapper;

        @Setup(Level.Trial)
        public void init() throws Exception {
            MapperFactory factory = new MapperFactory();
            mapper = factory.createMapper(NestedDump.class);
        }
    }

    @Benchmark
    public void test(BmState state, Blackhole blackhole) {
        Map<String, Object> source = state.source;
        NestedDump dump = state.mapper.serializeToObject(source);
        blackhole.consume(dump);
    }
}
