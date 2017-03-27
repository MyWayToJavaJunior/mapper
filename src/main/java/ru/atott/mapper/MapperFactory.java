package ru.atott.mapper;

import javassist.ClassPool;
import javassist.CtClass;
import ru.atott.mapper.builder.MapperClassBuilder;
import ru.atott.mapper.convertion.DefaultValueConverter;
import ru.atott.mapper.convertion.ValueConverter;
import ru.atott.mapper.convertion.ValueProducer;
import ru.atott.mapper.introspection.BeanField;
import ru.atott.mapper.introspection.BeanIntrospection;
import ru.atott.mapper.introspection.IntrospectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("unchecked")
public class MapperFactory {

    private static Map<Class, Mapper> cache = new HashMap<>();

    private static final Object monitor = new Object();

    private ValueConverter valueConverter;

    public MapperFactory() {
        this.valueConverter = initializeValueConverter();
    }

    public <T> Mapper<T> createMapper(Class<T> tClass) throws Exception {
        tClass = Objects.requireNonNull(tClass);

        Mapper objectSerializer = cache.get(tClass);

        if (objectSerializer == null) {
            synchronized (monitor) {
                objectSerializer = cache.get(tClass);

                if (objectSerializer == null) {
                    ClassPool classPool = ClassPool.getDefault();

                    BeanIntrospection beanIntrospection = new BeanIntrospection(classPool, tClass);
                    beanIntrospection.assertIsBean();

                    MapperClassBuilder builder = new MapperClassBuilder()
                            .setClassPool(classPool)
                            .settClass(tClass)
                            .setValueConverter(valueConverter);

                    Map<BeanField, ValueProducer> valueProducers = new HashMap<>();
                    beanIntrospection.getBeanFields().forEach(beanField -> {
                        Optional<ValueProducer> valueProducer = getValueProducer(beanField);

                        if (valueProducer.isPresent()) {
                            builder.addValueProducer(beanField);
                            valueProducers.put(beanField, valueProducer.get());
                        }
                    });

                    CtClass ctClass = builder.build();

                    objectSerializer = (Mapper<T>) ctClass.toClass().newInstance();
                    Field vcField = objectSerializer.getClass().getField("vc");
                    vcField.set(objectSerializer, valueConverter);

                    Mapper finalObjectSerializer = objectSerializer;
                    valueProducers.forEach((beanField, valueProducer) -> {
                        try {
                            Field vpField = finalObjectSerializer.getClass().getField(IntrospectionUtils.getValueProducerFieldName(beanField));
                            vpField.set(finalObjectSerializer, valueProducer);
                        } catch (Exception e) {
                            throw new RuntimeException(e.getMessage(), e);
                        }
                    });

                    cache.put(tClass, objectSerializer);
                }
            }
        }

        return objectSerializer;
    }

    protected Optional<ValueProducer> getValueProducer(BeanField field) {
        return Optional.empty();
    }

    protected ValueConverter initializeValueConverter() {
        return new DefaultValueConverter(this);
    }
}
