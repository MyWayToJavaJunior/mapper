package ru.atott.mapper.bm.creation;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;
import ru.atott.mapper.bm.BaseBmTest;
import ru.atott.mapper.dump.SimpleDump;

/**
 * 6.7 ns
 */
public class NativeCreationObjectBmTest extends BaseBmTest {

    @Benchmark
    public void test(Blackhole blackhole) {
        SimpleDump dump = new SimpleDump();
        blackhole.consume(dump);
    }
}
