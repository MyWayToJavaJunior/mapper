package ru.atott.mapper.test;

import org.junit.Test;
import ru.atott.mapper.Mapper;
import ru.atott.mapper.MapperFactory;
import ru.atott.mapper.dump.SimpleDump;
import ru.atott.mapper.introspection.NotBeanException;

import java.io.Serializable;

public class BeanAssertionTest {

    @Test
    public void test0() throws Exception {
        MapperFactory factory = new MapperFactory();
        factory.createMapper(ValidBean.class);
    }

    @Test(expected = NotBeanException.class)
    public void test1() throws Exception {
        MapperFactory factory = new MapperFactory();
        Mapper<InvalidBean1> serializer = factory.createMapper(InvalidBean1.class);
    }

    @Test(expected = NotBeanException.class)
    public void test2() throws Exception {
        MapperFactory factory = new MapperFactory();
        Mapper<InvalidBean2> serializer = factory.createMapper(InvalidBean2.class);
    }

    @Test(expected = NotBeanException.class)
    public void test3() throws Exception {
        MapperFactory factory = new MapperFactory();
        factory.createMapper(InvalidBean3.class);
    }

    @Test(expected = NotBeanException.class)
    public void test4() throws Exception {
        MapperFactory factory = new MapperFactory();
        factory.createMapper(Serializable.class);
    }

    @Test(expected = NotBeanException.class)
    public void test5() throws Exception {
        MapperFactory factory = new MapperFactory();
        factory.createMapper(Enum.class);
    }

    @Test(expected = NotBeanException.class)
    public void test6() throws Exception {
        MapperFactory factory = new MapperFactory();
        factory.createMapper(InvalidBean4.class);
    }

    @Test(expected = NotBeanException.class)
    public void test7() throws Exception {
        MapperFactory factory = new MapperFactory();
        factory.createMapper(InvalidBean5.class);
    }

    @Test(expected = NotBeanException.class)
    public void test8() throws Exception {
        MapperFactory factory = new MapperFactory();
        factory.createMapper(InvalidBean6.class);
    }

    @Test
    public void test9() throws Exception {
        MapperFactory factory = new MapperFactory();
        factory.createMapper(ValidBean7.class);
    }

    @Test(expected = NotBeanException.class)
    public void test10() throws Exception {
        MapperFactory factory = new MapperFactory();
        factory.createMapper(InvalidBean8.class);
    }

    public static class ValidBean {

    }

    private static class InvalidBean1 extends SimpleDump {

    }

    private static class InvalidBean2 implements Serializable {

    }

    private static class InvalidBean3 {

    }

    public static class InvalidBean4 {

        public InvalidBean4(String test) { }
    }

    public static class InvalidBean5 {

        private InvalidBean5() { }
    }

    public static class InvalidBean6 {

        private Boolean field;

        public void setField(Boolean field) {
            this.field = field;
        }
    }

    public static class ValidBean7 {

        private boolean field;

        public boolean isField() {
            return field;
        }

        public void setField(boolean field) {
            this.field = field;
        }
    }

    public static class InvalidBean8 {

        private Boolean field;

        public Boolean getField() {
            return field;
        }
    }
}
