package ru.atott.mapper.bm.oneFieldMap;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import ru.atott.mapper.Mapper;
import ru.atott.mapper.MapperFactory;
import ru.atott.mapper.bm.BaseBmTest;
import ru.atott.mapper.dump.OneFieldDump;

import java.util.HashMap;
import java.util.Map;

/**
 * 16.615 ns
 */
public class JavassistOneFieldMapTest extends BaseBmTest {

    @State(Scope.Benchmark)
    public static class BmState {

        Map<String, Object> source = new HashMap<String, Object>() {{
            put("id", "idValue");
        }};

        Mapper<OneFieldDump> serializer;

        @Setup(Level.Trial)
        public void init() throws Exception {
            MapperFactory factory = new MapperFactory();
            serializer = factory.createMapper(OneFieldDump.class);
        }
    }

    @Benchmark
    public void test(BmState state, Blackhole blackhole) {
        OneFieldDump bean = state.serializer.serializeToObject(state.source);
        blackhole.consume(bean);
    }
}
