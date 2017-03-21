package ru.atott.mapper.bm.oneFieldMap;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import ru.atott.mapper.bm.BaseBmTest;
import ru.atott.mapper.dump.OneFieldDump;

import java.util.HashMap;
import java.util.Map;

/**
 * 11.950 ns
 */
public class NativeOneFieldMapTest extends BaseBmTest {

    @State(Scope.Benchmark)
    public static class BmState {

        Map<String, Object> source = new HashMap<String, Object>() {{
            put("id", "idValue");
        }};
    }

    @Benchmark
    public void test(BmState state, Blackhole blackhole) {
        OneFieldDump bean = new OneFieldDump();
        bean.setId((String) state.source.get("id"));
        blackhole.consume(bean);
    }
}
