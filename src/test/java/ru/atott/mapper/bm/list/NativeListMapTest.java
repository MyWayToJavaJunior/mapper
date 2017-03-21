package ru.atott.mapper.bm.list;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import ru.atott.mapper.bm.BaseBmTest;
import ru.atott.mapper.dump.ListDump;
import ru.atott.mapper.dump.OneFieldDump;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 128 ns
 */
public class NativeListMapTest extends BaseBmTest {

    @State(Scope.Benchmark)
    public static class BmState {

        Map<String, Object> source = new HashMap<String, Object>() {{
            put("list1", Arrays.asList("1", "2"));
        }};
    }

    @Benchmark
    public void test(BmState state, Blackhole blackhole) {
        Map<String, Object> source = state.source;

        ListDump dump = new ListDump();
        dump.setList2(Optional.of(Collections.emptyList()));
        dump.setList1((List<String>) ((List) source.get("list1")).stream()
                .map(String::valueOf)
                .collect(Collectors.toList()));

        blackhole.consume(dump);
    }
}
