package ru.atott.mapper.bm.creation;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;
import ru.atott.mapper.bm.BaseBmTest;
import ru.atott.mapper.dump.SimpleDump;

/**
 * 12.6 ns
 */
public class ReflectionCreationObjectBmTest extends BaseBmTest {

    @Benchmark
    public void test(Blackhole blackhole) throws IllegalAccessException, InstantiationException {
        SimpleDump dump = SimpleDump.class.newInstance();
        blackhole.consume(dump);
    }
}
