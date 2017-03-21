package ru.atott.mapper.bm.creation;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import ru.atott.mapper.Mapper;
import ru.atott.mapper.MapperFactory;
import ru.atott.mapper.bm.BaseBmTest;
import ru.atott.mapper.dump.SimpleDump;

/**
 * 6.5 ns
 * 13 ns
 */
public class JavassistCreationObjectBmTest extends BaseBmTest {

    @State(Scope.Benchmark)
    public static class BenchmarkState {

        private Mapper<SimpleDump> serializer;

        @Setup(Level.Trial)
        public void initialize() throws Exception {
            MapperFactory factory = new MapperFactory();
            serializer = factory.createMapper(SimpleDump.class);
        }
    }

    @Benchmark
    public void test1(BenchmarkState state, Blackhole blackhole) {
        SimpleDump simpleDump = state.serializer.newInstance();
        blackhole.consume(simpleDump);
    }

    @Benchmark
    public void test2(Blackhole blackhole) throws Exception {
        MapperFactory factory = new MapperFactory();
        Mapper<SimpleDump> serializer = factory.createMapper(SimpleDump.class);
        SimpleDump simpleDump = serializer.newInstance();
        blackhole.consume(simpleDump);
    }
}
